package com.github.sbellus.fitnesse.codeprettifier;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONObject;

import fitnesse.html.HtmlTag;
import fitnesse.html.HtmlText;
import fitnesse.html.RawHtml;
import fitnesse.wikitext.parser.Matcher;
import fitnesse.wikitext.parser.Maybe;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Rule;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolType;
import fitnesse.wikitext.parser.Translation;
import fitnesse.wikitext.parser.Translator;

public class ListingSymbol extends SymbolType implements Rule, Translation {
    private static final Pattern OptionsPattern = Pattern.compile("!listing[ \t]+([a-z]*)[ \t]*.*");
    
    public static SymbolType make() {
        return new ListingSymbol();
    }
    
    public ListingSymbol() {
        super("Listing");
        wikiMatcher(new Matcher().string("!listing").endsWith(new char[] {'(', '{', '['}));
        wikiRule(this);
        htmlTranslation(this);
    }

    public Maybe<Symbol> parse(Symbol current, Parser parser) {
       
        String content = current.getContent();
        // check keyword store in content again more strictly
        if (! (content.charAt(8) == ' ' || content.charAt(8) == '(' || content.charAt(8) == '{' || content.charAt(8) == '['))
        {
            // not my symbol. I expect "!listing ".
            return Symbol.nothing;
        }
        
        // get options
        java.util.regex.Matcher m = OptionsPattern.matcher(content);
        String type = "";
        if (m.find())
        {
            type = m.group(1);
        }

        // get content
        char beginner = content.charAt(content.length() - 1);
        content = parser.parseLiteral(closeType(beginner));
        if (parser.atEnd())
        {
            return Symbol.nothing;
        }
        
        current.putProperty("type", type);
        
        if (type.equals("json"))
        {
            content = IdentJson(content);
        }
        else if (type.equals("xml"))
        {
            content = IdentXml(content);
        }
            
        current.putProperty("content", content);
        
        return new Maybe<Symbol>(current);
    }

    private String IdentXml(String content) {
        String identedXml = content;
        
        try {
            
            Source xmlInput = new StreamSource(new StringReader(trim(content)));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(xmlInput, xmlOutput);
            identedXml = xmlOutput.getWriter().toString();
        } catch (Exception e) {
            // this is case of wrong xml
            identedXml += "\n<!--XML has error : " + e.getMessage() + " --!>";
        }
        
        return identedXml;
    }

    private String IdentJson(String content) {
        String identedJson = content;
        
        try {
            JSONObject json = new JSONObject(content); // Convert text to object
            identedJson = json.toString(4);
        } catch (Exception e) {
            // this is case of wrong json
            identedJson += "\n// JSON has error : " + e.getMessage();
        }

        return identedJson;
    }

    private static SymbolType closeType(char beginner) {
        return beginner == '[' ? SymbolType.CloseBracket
                : beginner == '{' ? SymbolType.CloseBrace : SymbolType.CloseParenthesis;
    }

    private String trim(String input) {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        StringBuffer result = new StringBuffer();
        try {
            String line;
            while ( (line = reader.readLine() ) != null)
                result.append(line.trim());
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String toTarget(Translator translator, Symbol symbol) {
        
        HtmlTag listingSection = new HtmlTag("div");
        String divclassValue = "listing";
        if (! symbol.getProperty("type").isEmpty())
        {
            divclassValue += "_" + symbol.getProperty("type"); 
        }
        listingSection.addAttribute("class", divclassValue);
        listingSection.addAttribute("type", symbol.getProperty("type"));
        String originalContent = trim(symbol.getProperty("content"));
        originalContent = originalContent.replaceAll("&", "&amp;");
        originalContent = originalContent.replaceAll("\"", "&quot;");
        listingSection.addAttribute("originalcontent", originalContent);
        listingSection.add(new RawHtml("<script src=\"/files/fitnesse/code-prettify/run_prettify.js\" type=\"text/javascript\"></script>"));
        HtmlTag pre = new HtmlTag("pre");
        
        String prettyprintclass = "prettyprint";
        if (! symbol.getProperty("type").isEmpty())
        {
            prettyprintclass += " lang-" + symbol.getProperty("type"); 
        }
        pre.addAttribute("class", prettyprintclass);
        pre.add(new HtmlText(symbol.getProperty("content")));
        listingSection.add(pre);

        return listingSection.html();
    }
}
