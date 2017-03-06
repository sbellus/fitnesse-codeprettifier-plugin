# Overview

The project extends [Fitnesse](http://www.fitnesse.org/) wiki with colorized code.
It uses [code-prettify](https://github.com/google/code-prettify). The [code-prettify](https://github.com/google/code-prettify) is embedded in jar file. When plugin is loaded it copies [code-prettify](https://github.com/google/code-prettify) into FitNesseRoot/files/fitnesse to be accessible by html pages. The [code-prettify](https://github.com/google/code-prettify) is modified to use style  ```cptag``` instead of ```tag``` due to FitNesse style ```tag```.

# Installation

1. Copy jar file from [this project Releases](https://github.com/sbellus/fitnesse-codeprettifier-plugin/releases) to plugins directory of your Fitnesse.
2. Restart Fitnesse

## Configuration

No Configuration.

# Usage

After installation and Fitnesse restart you should be able to use command on wiki
```
!listing [language] {
some code
}
```
The attribute ```laguage``` is optional. Can be one of ```bsh|c|cc|cpp|cs|csh|cyc|cv|htm|html|java|js|m|mxml|perl|pl|pm|py|rb|sh|xhtml|xml|xsl|json```.
When ```xml``` or ```json``` is used then plugin also idents given text.
