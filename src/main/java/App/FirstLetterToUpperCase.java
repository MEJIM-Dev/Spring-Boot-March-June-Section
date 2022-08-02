package App;

import java.util.ArrayList;
import java.util.Arrays;

public class FirstLetterToUpperCase {
    public static void main(String[] args) {
//        String test = "abc d e f ghij klmo";
        }

    public static void ternaryOop(String args) {
        String bes = "a"=="b" ? "true" : "false";
    }

    public static String FirstToCap(String args) {
        String[] test2 = args.split("\s");
        int spaces = test2.length-1;

        args="";

        for (String data: test2) {
            char[] holder = data.toCharArray();
            char first = Character.toUpperCase(holder[0]);
//            if(first=='') {
//                holder[0] = first;
//            }
            if(!Character.isDigit(first)) holder[0] =first;
            for (char i: holder) {
                args+=i;
            }

            if(spaces>0){
                spaces--;
                args+=" ";
            }

//            System.out.println(holder[0]);
            System.out.println(args);
        }
        return args;
    }

    }
