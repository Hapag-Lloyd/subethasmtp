function suppressSiteWarnings() {
	cat < /dev/stdin \
	| grep --invert-match --perl-regexp "^Warning:  Unable to find a URL to the parent project\\. The parent menu will NOT be added\\.$"
}

cat < /dev/stdin \
| suppressSiteWarnings
