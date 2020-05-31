package parse;

import java.util.Arrays;

/**
 * This was actually not my code originally, i just borrowed it and modified 
 * slightly to fit my needs, because it is light, easy to use and just works.
 * All the bugs and bad solutions go to the original creator :)
 */

public class ExpressionParser {
    
    public double startParse(String expr){
        double res = 0;
        
        if(expr.contains("(") || expr.contains(")")) {
            res = parseR(expr);
        } else
            res = parseAsArray(expr.split(" "));
        
        return res;
    }
    
    private double parseR(String innerExpr){
        if(innerExpr.contains("(") || innerExpr.contains(")")) {
            int open = innerExpr.indexOf("(");
            int close = innerExpr.lastIndexOf(")");
            if(open == -1 && close != -1 || open != -1 && close == -1 || close >= open) {
                System.out.println("Some parenthesises are not paired properly!");
                return 0;
            } else {
                String subExpr = innerExpr.substring(open + 2, close - 1);
                if(subExpr.contains("(") || subExpr.contains(")")) {
                    return parseAsArray(innerExpr.replace(innerExpr.substring(open, close), parseR(subExpr) + "").split(" ")); 
                } else {
                    return parseAsArray(innerExpr.replace(innerExpr.substring(open, close), parseAsArray(subExpr.split(" ")) + "").split(" "));
                }
            }
        } else {
            return parseAsArray(innerExpr.split(" "));
        }
    }
    
    private double parseAsArray(String[] expr){
        if(expr.length == 3) return calcArr(expr);
        if(expr.length == 2) return calcArr(new String[]{expr[0], expr[1], " "});
        else {
            String init = this.appendArray(expr, " ");
            if(init.contains("^")) {
                int findex = findLast(expr, "^");
                String[] arrat = {expr[findex - 1], expr[findex], expr[findex + 1]};
                double repVar = calcArr(arrat);
                String torep = this.appendArray(arrat, " ");
                String proceed = init.replace(torep, repVar + "");
                return parseAsArray(proceed.split(" "));
            } 
            else if(init.contains("//")) {
                int findex = findLast(expr, "//");
                String[] arrat = {expr[findex - 1], expr[findex], expr[findex + 1]};
                double repVar = calcArr(arrat);
                String torep = this.appendArray(arrat, " ");
                String proceed = init.replace(torep, repVar + "");
                return parseAsArray(proceed.split(" "));
            } 
            else if(init.contains("*")) {
                int findex = findFirst(expr, "*");
                String[] arrat = {expr[findex - 1], expr[findex], expr[findex + 1]};
                double repVar = calcArr(arrat);
                String torep = this.appendArray(arrat, " ");
                String proceed = init.replace(torep, repVar + "");
                return parseAsArray(proceed.split(" "));
            }
            else if(init.contains("/")) {
                int findex = findFirst(expr, "*");
                String[] arrat = {expr[findex - 1], expr[findex], expr[findex + 1]};
                double repVar = calcArr(arrat);
                String torep = this.appendArray(arrat, " ");
                String proceed = init.replace(torep, repVar + "");
                return parseAsArray(proceed.split(" "));
            }
            else {
                String[] arrat = new String[]{expr[0], expr[1], expr[2]};
                double repVar = calcArr(arrat);
                String torep = this.appendArray(arrat, " ");
                String proceed = init.replace(torep, repVar + "");
                return parseAsArray(proceed.split(" "));
            }
        }
    }
    
    private double calcArr(String[] processedExpr){
        double resultBuffer = 0;
        
        for(int i = 0; i < processedExpr.length - 2;){
            
            String A = processedExpr[i];
            if(A.contains("(")) A = A.replace("(", "");
            if(A.contains(")")) A = A.replace(")", "");
            
            String B = processedExpr[i+2];
            if(B.contains("(")) B = B.replace("(", "");
            if(B.contains(")")) B = B.replace(")", "");
            
            double a = Double.parseDouble(A);
            double b = 0;
            try {
                b = Double.parseDouble(B);
            } catch (Exception ex) { 
                // We don't really need it, but we accept that for some operations
                // we can have only a and b will be some nonsense.
            }
            String operation = processedExpr[i+1];
            
            switch(operation) {
                case "+":
                    resultBuffer += a + b;
                break;
                case "-":
                    resultBuffer += a - b;
                break;
                case "*":
                    resultBuffer += a * b;
                break;
                case "/":
                    resultBuffer += a / b;
                break;
                case "^":
                    resultBuffer += Math.pow(a, b);
                break;
                case "//":
                    resultBuffer += ExpressionParser.Root(a, b);
                break;
                case "sin":
                    resultBuffer += Math.sin(a);
                break;
                case "cos":
                    resultBuffer += Math.cos(a);
                break;
                case "tg":
                    resultBuffer += Math.tan(a);
                break;
                case "ctg":
                    resultBuffer += 1 / Math.tan(a);
                break;
            }
            
            i+=3;
        }
        
        return resultBuffer;
    }
    
    public static double Root(double a, double b){
        double result = Math.pow(Math.exp (1/a),Math.log(b));
        
        // Since cube root from 8 is not 2 but 1.9999999999999998 instead
        // this is a neccesary (but not safe) check whether we need to
        // round the value to the nearest integer (long in our case)
        double diff = Math.round(result) - result;
        double modResult = diff < 0 ? -1*diff : diff;
        return modResult > 0.0000000000000005 ? result : Math.round(result);
    }
    
    private String appendArray(String[] arr, String dlim) {
        return Arrays
                .asList(arr)
                .stream()
                .map((s) -> {
                    return s + dlim;
                })
                .reduce(String::concat)
                .orElse(" ")
                .trim();
    }
    
    private int countChar(String s, char chr) {
        int counter = 0;
        if(s.contains(chr + "")) {
            String str = s;
            while(str.contains(chr + "")){
                str = str.substring(str.indexOf(chr + ""));
                counter++;
            }
        }
        return counter;
    }
    
    private <T> int findFirst(T[] arr, T obj) {
        int i = 0;
        for(T t : arr) {
            if(t.equals(obj)) break;
            i++;
        }
        if(i == arr.length - 1) i = -1;
        return i;
    }
    
    private <T> int findLast(T[] arr, T obj) {
        int i = 0;
        boolean found = false;
        for(i = arr.length - 1; i >= 0; i--){
            if(arr[i].equals(obj)) {
                found = true;
                break;
            }
        }
        if(!found) i = -1;
        return i;
    }
}
