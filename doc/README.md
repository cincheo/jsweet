# JSweet documentation

This sub-project contains JSweet documentation.

## How to build

To generate the markdown language specifications from the Latex source file with [Pandoc](http://pandoc.org/):

```
> cd doc
> pandoc -r latex -w markdown_github --base-header-level=2 -s --toc --number-sections -B header.md -o jsweet-language-specifications.md jsweet-language-specifications.tex
```

Generate PDF version (you will need to install [MikTex](https://miktex.org/howto/install-miktex))

```
> pandoc --pdf-engine=xelatex -r latex --base-header-level=3 -o jsweet-language-specifications.pdf jsweet-language-specifications.tex
```

Note that the following command will output the document in HTML:

```
> pandoc -r latex -w html5 --base-header-level=3 -o jsweet-language-specifications.html jsweet-language-specifications.tex
```

## License

This documentation is licensed under the Creative Commons license: CC-BY-SA. 