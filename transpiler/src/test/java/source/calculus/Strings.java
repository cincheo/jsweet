package source.calculus;

import static jsweet.util.Lang.$export;

public class Strings {

    public static void main(String[] args) {
        String str = "";
        str = str + (char)(97);
        $export("str_plus_char_casted_int", str);

        str = "";
        str = str + (97);
        $export("str_plus_int", str);
        
        char c = 'd';
        str = "";
        str = str + c;
        $export("str_plus_char", str); 
        
        str = "";
        str += (char)(97);
        $export("str_plus_equal_casted_int", str);
        
        str = "";
        str += 97;
        $export("str_plus_equal_int", str);
    }
    
}