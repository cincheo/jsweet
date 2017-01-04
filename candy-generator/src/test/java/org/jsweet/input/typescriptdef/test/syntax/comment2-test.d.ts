/**
 * Main doc
 */
interface GoogleApiPageSpeedOnlineRuleResource {
    /**
     * Localized name of the rule, intended for presentation to a user.
     */
    localizedRuleName: string;
    /**
     * The score (0-100) for this rule. The rule score indicates how well a page implements the recommendations for the given rule.
     */
    ruleScore: number;
    /**
     * The impact (unbounded floating point value) that implementing the suggestions for this rule would have on making the page faster.
     */
    ruleImpact: number;
    /**
     * List of blocks of URLs. Each block may contain a heading and a list of URLs. Each URL may optionally include additional details.
     */
    urlBlocks: {
        /**
         * Heading to be displayed with the list of URLs.
         */
        header: {
            /**
             * A localized format string with $N placeholders, where N is the 1-indexed argument number.
             */
            format: string;
            /**
             * List of arguments for the format string.
             */
            args: {
                /**
                 * Type of argument. One of URL, STRING_LITERAL, INT_LITERAL, BYTES, or DURATION.
                 */
                type: string;
                /**
                 * Argument value, as a localized string.
                 */
                value: string;
            }[];
        }
        /**
         * List of entries that provide information about URLs in the URL block. Optional.
         */
        urls: {
            /**
             * A format string that gives information about the URL, and a list of arguments for that format string.
             */
            result: {
                /**
                 * A localized format string with $N placeholders, where N is the 1-indexed argument number. For example: 'Minifying the resource at URL $1 can save $2 bytes'.
                 */
                format: string;
                /**
                 * List of arguments for the format string.
                 */
                args: {
                    /**
                     * Type of argument. One of URL, STRING_LITERAL, INT_LITERAL, BYTES, or DURATION.
                     */
                    type: string;
                    /**
                     * Argument value, as a localized string.
                     */
                    value: string;
                }[];
            }
            /**
             * List of entries that provide additional details about a single URL. Optional.
             */
            details: {
                /**
                 * A localized format string with $N placeholders, where N is the 1-indexed argument number.
                 */
                format: string;
                /**
                 * List of arguments for the format string.
                 */
                args: {
                    /**
                     * Type of argument. One of URL, STRING_LITERAL, INT_LITERAL, BYTES, or DURATION.
                     */
                    type: string;
                    /**
                     * Argument value, as a localized string.
                     */
                    value: string;
                }[];
            }[];
        }[];
    }[];
}

/**
 * Another doc
 */
module m {
    /**
     * Yet another doc
     */
    interface I {
    }   
}
                    /**
                     * 
                     */