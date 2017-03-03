package com.github.sbellus.fitnesse.codeprettifier;

import fitnesse.plugins.PluginException;
import fitnesse.plugins.PluginFeatureFactoryBase;
import fitnesse.wikitext.parser.SymbolProvider;

/**
 * Register plantuml symbol.
 */
public class CodePrettifierPlugin extends PluginFeatureFactoryBase {
    public CodePrettifierPlugin() {
    }
    
    public void registerSymbolTypes(SymbolProvider symbolProvider) throws PluginException {
        symbolProvider.add(ListingSymbol.make());
    }
}
