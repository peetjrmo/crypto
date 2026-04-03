/**
 * Will encrypt files and messages using 7-bit ASCII values and a variety of cryptography methods, i.e,
 * 
 * - Ceasar Cipher
 * - Homophonic Cipher
 * - Vigenere Cipher
 * - Affine Cipher
 * - Playfair Cipher
 * - Hill Cipher
 * 
 * @author MPJ
 * @version 2.22.26
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;



public class App {

    static Map<Character, Integer> alpha = new HashMap<>();
    static int key;

    /**
     * [...]
     * 
     * @param args
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        String plain;
        String cipher;
        int opt;

        opt = menu();

        switch (opt) {

            case 1 :    plain = getMessage();

                        key = getKey();

                        cipher = ceasar(plain);
                        plain = ceasarX(cipher, key);

                        break;

            case 2 :    Map<Character, ArrayList<Integer>> map = new HashMap<>();
                        ArrayList<Integer> cipherList = new ArrayList<>();

                        plain = parseString(getMessage());

                        cipherList = homophonic(plain, map);
                        plain = homoPhonicX(cipherList, map);

                        System.out.println("Cipher : " + cipherList.toString());
                        System.out.println("Plaintext : " + plain);

                        break;

            case 3 :    int num1, num2;
                        String keyword = getKeyword().toUpperCase();
                        plain = parseString(getMessage()).toUpperCase();
                        String mirror = getMirror(plain, keyword);
                        
                        cipher = vigenere(plain, mirror);
                        print("Plain : " + plain);
                        
                        plain = vigenereX(cipher, getMirror(plain, keyword));
                        print("CIPHER : " + cipher);
                        print("PLAIN : " + plain);
                        break;

            case 4 :    plain = parseString(getMessage());
                            
                        do {

                            System.out.print("Enter first number: ");
                            num1 = in.nextInt();

                            System.out.print("Enter second number: ");
                            num2 = in.nextInt();

                            if (isCoPrime(num1, num2)) {

                                System.out.println(num1 + " and " + num2 + " are Co-prime numbers");

                            } else {

                                System.out.println(num1 + " and " + num2 + " are NOT Co-prime numbers");

                            }

                        } while (!(isCoPrime(num1, num2)));
        
                        affine(plain, num1, num2);

            case 5 :    plain = parseString(getMessage()).toUpperCase();
                        print(plain);
                        System.exit(0);
                        Map<String, Integer> cipherMap = new LinkedHashMap<>();
                        cipherMap = playfair(plain);
                        break;

            case 6 :    
                        hill("");


            case 7 :    print("Bye!");
                        break;
        }
    }

    /** 
     * Will encrypt text using the Ceasar (Substitution) encryption scheme.
     * 
     * @param plain plaintext message
     * @return ciphertext message
     */
    public static String ceasar(String plain) {

        String cipher = "";
        char ascii;
        int index;

        for (int i = 0; i < plain.length(); i++) {
           
            index = plain.charAt(i);    
            index += key;           
            
            if (index > 255) {
                
                index = index % 255;
            }

            ascii = getAscii(index);
            cipher += ascii;
        }

        return cipher;
    }

    /**
     * Will decrypt messages encrypted with the Ceasar cipher and paired key.
     * 
     * @param cipher    ciphertext message
     * @param key       encryption key
     * @return          plaintext message
     */
    public static String ceasarX(String cipher, int key) {

        String plain = "";
        char ascii;
        int index;

        for (int i = 0; i < cipher.length(); i++) {

            index = cipher.charAt(i);
            index -= key;

            if (index < 0) {

                index = index % 255;
                index += Math.abs(255);                 // True modulo conversion
                
            }
            ascii = getAscii(index);
            plain += ascii;
        }

        return plain;
    }

    /**
     * Will encrypt text using the homophonic encryption scheme.
     * 
     * @param plain plaintext message
     * @param map   plaintext character to ciphertext character mappings
     * @return      ciphertext message
     */
    public static ArrayList<Integer> homophonic(String plain, Map<Character, ArrayList<Integer>> map) {

        ArrayList<Integer> vals = new ArrayList<>();                // List of random ints free of duplicates
        ArrayList<Integer> cipher = new ArrayList<>();
         ArrayList<Integer> temp;

        Random rand = new Random();
        int num;
        for (int i = 0; i < 384; ++i) {                 // Create list of 384 (3 per ASCII character) random integers [0, 1000]

            do {

                num  = rand.nextInt(1000);

            } while (exists(num, vals));

            vals.add(num);  
        }

        for (int i = 0; i < 128; i++) {                 // Map each ASCII character [0, 128] to an arraylist and update map

            temp = getSet(vals);
            updateVals(vals);
            map.put((char)i, new ArrayList<>(temp)); 
        }

        System.out.println("MAP : " + map);
       

        for (int i = 0; i < plain.length(); ++i) {

            cipher.add(map.get(plain.charAt(i)).get(getRand(3)));  // = "### ###"
        }

        return cipher;
    }

    /**
     * Will decrypt text encrypted with the Homophonic cipher.
     * 
     * @param cipher    ciphertext message
     * @param map       plaintext character to ciphertext character mappings
     * @return          plaintext message
     */
    public static String homoPhonicX(ArrayList<Integer> cipher, Map<Character, ArrayList<Integer>> map) {

        Set<Map.Entry<Character, ArrayList<Integer>>> entrySet = map.entrySet();
        String plain = "";

        for (int i = 0; i < cipher.size(); ++i) {

            for (Map.Entry<Character, ArrayList<Integer>> entry : entrySet) {

                if (entry.getValue().contains(cipher.get(i))) {

                    plain += entry.getKey();
                }
            }
        }
        
        return plain;
    }

    public static String affine(String plain, int a, int b) {

        String cipher = "";

        
        for (int i = 0; i < plain.length(); ++i) {
            // cipher += (a*alpha. + b) mod 26
            cipher += (char)(((a * alpha.get(plain.charAt(i)) + b) % 26) + 65);
        }
        System.out.println("CIPHER: " + cipher);

        return cipher;
    }

    public static String affineX(String cipher, int a, int b) {

        return "";
    }


    /**
     * Will encrypt text using the Playfair encryption scheme.
     * 
     * @param plain plaintext message
     * @return      ciphertext message
     */
    public static Map<String, Integer> playfair(String plain) {

        final int MAX_ROWS = 5;
        final int MAX_COLUMNS = 5;
        final int ROW = 0;
        final int COLUMN = 1;
        final int FIRST = 0;
        final int SECOND = 1;

        int curr = 0;
        String cipherString = "";
        char[][] grid = new char[MAX_ROWS][MAX_COLUMNS];
        Map<Character, ArrayList<Integer>> coordinates = new HashMap<>();
        Map<String, Integer> cipher = new LinkedHashMap<>();
        ArrayList<Character> alpha = new ArrayList<>();
        ArrayList<Integer> temp;
        ArrayList<String> pairs;
        ArrayList<ArrayList<Integer>> openCoords = new ArrayList<>();
        boolean padded = false;
        
        for (int i = 65; i < 91; i++) {

            if (i != 74) {

                alpha.add((char)i);
            }  
        }

        for (int i = 0; i < MAX_ROWS; ++i) {
            
            for (int j = 0; j < MAX_COLUMNS; ++j) {

                temp = new ArrayList<>(Arrays.asList(i,j));
                openCoords.add(temp);
            }
        }

        for (int i = 0; i < alpha.size(); ++i) {

            temp = openCoords.get(getRand(openCoords.size()));
            grid[temp.get(0)][temp.get(1)] = alpha.get(curr);
            openCoords.remove(temp);
                
            coordinates.put(alpha.get(curr), new ArrayList<>(temp));

            curr++;
        }

        for (char[] row : grid) {

            for (char value : row) {

                System.out.print(value + "     "); // formatted spacing
            }
            System.out.println();
        }

        if (plain.length() % 2 == 1) {

            plain += 'Z';
            padded = true;
        }
        pairs = toPairs(plain);
        System.out.println(pairs);


        for (int i = 0; i < pairs.size(); ++i) {

            int firstRow        = coordinates.get(pairs.get(i).charAt(FIRST)).get(ROW);
            int firstColumn     = coordinates.get(pairs.get(i).charAt(FIRST)).get(COLUMN);
            int secondRow       = coordinates.get(pairs.get(i).charAt(SECOND)).get(ROW);
            int secondColumn    = coordinates.get(pairs.get(i).charAt(SECOND)).get(COLUMN);

            if (cipher.containsKey(pairs.get(i))) {

                cipherString += cipher.get(pairs.get(i));
            }

            if (pairs.get(i).charAt(0) == pairs.get(i).charAt(1)) {
                
                cipher.put(pairs.get(i).charAt(0) + "X" + pairs.get(i).charAt(1), 0);
                pairs.set(i, pairs.get(i).charAt(0) + "X" + pairs.get(i).charAt(1));
                cipherString += pairs.get(i).charAt(0) + "X" + pairs.get(i).charAt(1);
                
                System.out.println(pairs);

               

            } else if (coordinates.get(pairs.get(i).charAt(FIRST)).get(ROW) == coordinates.get(pairs.get(i).charAt(SECOND)).get(ROW)) {         // if pair is in same row

                int newColumn;
                int newColumn2;

                if (coordinates.get(pairs.get(i).charAt(FIRST)).get(COLUMN) == 4) {

                    newColumn = 0;

                } else  {

                    newColumn = coordinates.get(pairs.get(i).charAt(FIRST)).get(COLUMN) + 1;
                }

                if (coordinates.get(pairs.get(i).charAt(SECOND)).get(COLUMN) == 4) {

                    newColumn2 = 0;

                } else  {

                    newColumn2 = coordinates.get(pairs.get(i).charAt(SECOND)).get(COLUMN) + 1;
                }
                
                cipher.put("" + grid[firstRow][newColumn] + grid[firstRow][newColumn2], 1);
                cipherString += "" + grid[firstRow][newColumn] + grid[firstRow][newColumn2];

            } else if (coordinates.get(pairs.get(i).charAt(FIRST)).get(COLUMN) == coordinates.get(pairs.get(i).charAt(SECOND)).get(COLUMN)) {   // if pair is in same column


                int newRow;
                int newRow2;
                
                if (coordinates.get(pairs.get(i).charAt(FIRST)).get(ROW) == 4) {

                    newRow = 0;

                } else  {

                    newRow = coordinates.get(pairs.get(i).charAt(FIRST)).get(ROW) + 1;
                }

                if (coordinates.get(pairs.get(i).charAt(SECOND)).get(ROW) == 4) {

                    newRow2 = 0;

                } else  {

                    newRow2 = coordinates.get(pairs.get(i).charAt(SECOND)).get(ROW) + 1;
                }

                cipher.put("" + grid[newRow][firstColumn] + grid[newRow2][secondColumn], 2);
                cipherString += "" + grid[newRow][firstColumn] + grid[newRow2][secondColumn];


            } else if (coordinates.get(pairs.get(i).charAt(FIRST)).get(ROW) != coordinates.get(pairs.get(i).charAt(SECOND)).get(ROW) &&
                        coordinates.get(pairs.get(i).charAt(FIRST)).get(COLUMN) != coordinates.get(pairs.get(i).charAt(SECOND)).get(COLUMN)) {  // if pair is in different column and row

                cipher.put("" + grid[firstRow][secondColumn] + grid[secondRow][firstColumn], 3);
                cipherString += "" + grid[firstRow][secondColumn] + grid[secondRow][firstColumn];
            }
            
        }

        System.out.println(cipher);
        System.out.println("Cipher String: " + cipherString);
        playfairX(cipher, grid, coordinates, padded);
        return cipher;
    }

    /**
     * Will decrypt text encrypted with the Playfair cipher.
     * 
     * @param cipher    ciphertext message
     * @param grid      2-d array of randomized alphabet
     * @param coord     (x,y) grid coordinates of alphabet 
     * @param padded    true if plaintext was padded with 'Z', false otherwise
     * @return          ciphertext message
     */
    public static String playfairX(Map<String, Integer> cipher, char[][] grid, Map<Character, ArrayList<Integer>> coord, boolean padded) {
        
        final int ROW = 0;
        final int COLUMN = 1;

        final int FIRST = 0;
        final int SECOND = 1;
        
        String plain = "";
        int algo;

        for (Map.Entry<String, Integer> entry : cipher.entrySet()) {
            
            algo = entry.getValue();
            switch (algo) {

                case 0:     plain += "" + entry.getKey().charAt(0) + entry.getKey().charAt(2);
                            break;

                case 1:     int oldColumn;
                            int oldColumn2;

                            if (coord.get(entry.getKey().charAt(FIRST)).get(COLUMN) == 0) {
                            
                                oldColumn = 4;
                            } else {

                                
                                oldColumn = (coord.get(entry.getKey().charAt(FIRST)).get(COLUMN)) - 1;
                            }

                            if (coord.get(entry.getKey().charAt(SECOND)).get(COLUMN) == 0) {
                            
                                oldColumn2 = 4;
                            
                            } else {
                                oldColumn2 = (coord.get(entry.getKey().charAt(SECOND)).get(COLUMN)) - 1;
                            }

                            plain += "" + grid[coord.get(entry.getKey().charAt(FIRST)).get(ROW)][oldColumn] + 
                                    grid[coord.get(entry.getKey().charAt(SECOND)).get(ROW)][oldColumn2];
                            break;


                case 2:     int oldRow;
                            int oldRow2;

                            if (coord.get(entry.getKey().charAt(FIRST)).get(ROW) == 0) {
                            
                                oldRow = 4;
                            } else {
                                
                                oldRow = (coord.get(entry.getKey().charAt(FIRST)).get(ROW)) - 1;
                            }

                            if (coord.get(entry.getKey().charAt(SECOND)).get(ROW) == 0) {
                            
                                oldRow2 = 4;
                            } else {
                                oldRow2 = (coord.get(entry.getKey().charAt(SECOND)).get(ROW)) - 1;
                            }

                            plain += "" + grid[oldRow][coord.get(entry.getKey().charAt(FIRST)).get(COLUMN)] + 
                                    grid[oldRow2][coord.get(entry.getKey().charAt(SECOND)).get(COLUMN)];
                            break;              // Same column


                case 3:     plain += "" + grid[coord.get(entry.getKey().charAt(FIRST)).get(ROW)][coord.get(entry.getKey().charAt(SECOND)).get(COLUMN)] + 
                                    grid[coord.get(entry.getKey().charAt(SECOND)).get(ROW)][coord.get(entry.getKey().charAt(FIRST)).get(COLUMN)]; 
                                    
                            break;

            }
        }

        if (padded) {

            plain = plain.substring(0, plain.length() - 1);
        }

        System.out.println("Plain: " + plain);
        
        return plain;
    }

    public static String vigenere(String plain, String mirror) {

        String cipher = "";

        for (int i = 0; i < plain.length(); ++i) {
            

            if ((int)plain.charAt(i) + (int)mirror.charAt(i) > 25) {

                cipher += (char)((((int)plain.charAt(i) + (int)mirror.charAt(i)) % 26) + 65);

            } else {

                cipher += (char)((int)plain.charAt(i) + (int)mirror.charAt(i));
            }
        }

        return cipher;
    }

    public static String vigenereX(String cipher, String mirror) {

        String plain = "";
        int index = 0;

        for (int i = 0; i < cipher.length(); ++i) {

            index = (int)cipher.charAt(i) - (int)mirror.charAt(i);
            System.out.println("Index : " + index);

            if (index < 0) {

                // -7 --> 84 = 91
                // -4 --> 87 = 91
                plain += (char)((index) + 91);

            } else {

                 plain += (char)((index) + 65);
            }
           
        }

        return plain;
    }
    

    /**
     * Will encrypt text using the Hill encryption scheme.
     * 
     * @param plain plaintext message
     * @return      ciphertext message
     */
    public static String hill(String plain) {

        return "";
    }

    /**
     * Will decrypt text encrypted with the Hill cipher.
     * 
     * @param cipher    ciphertext message
     * @return          plaintext message
     */
    public static String hillX() {

        return "";
    }

    public static String getMessage() {

        Scanner in = new Scanner(System.in);
        String message;

        System.out.print("Enter message : ");
        message = in.nextLine();

        return message;
    }

    /**
     * Will return an array list of integers (bypasses concurrent modification exception)
     * 
     * @param vals  list of integers
     * @return      list of integers
     */
    public static ArrayList<Integer> getSet(ArrayList<Integer> vals) {

        ArrayList<Integer> temp = new ArrayList<>();

        for (int i = 0; i < 3; ++i) {

            temp.add(vals.get(i));
        }

        return temp;
    }

    /**
     * Will remove first 3 integers from reference array list parameter(bypasses concurrent modification exception).
     * 
     * @param vals  array list of values to remove
     */
    public static void updateVals(ArrayList<Integer> vals) {

        if (vals.size() > 3) {
            for (int i = 0; i < 3; ++i) {
                
                vals.remove(0);
            }
        }  
    }

    /**
     * Will convert a string of plaintext characters into pairs, padding on a 'Z' if the length of the string is odd.
     * 
     * @param plain plaintext message
     * @return      array list of paired strings
     */
    public static ArrayList<String> toPairs(String plain) {

        ArrayList<String> pairs = new ArrayList<>();
        String temp = "";

        for (int i = 0; i < plain.length(); ++i) {

            
            temp += plain.charAt(i);

            if (i % 2 == 1) {

                pairs.add(temp);
                temp = "";
            }
        }

        return pairs;
    }

    /**
     * Will create a key for encryption by either prompting user for a value or by generating by random.
     * 
     * @return  key
     */
    public static int getKey() {

        Scanner in = new Scanner(System.in);
        int opt;
        
        System.out.print("1) Provide key\n2) Get random key\n\n");
        opt = in.nextInt();

        switch (opt) {

            case 1 :    System.out.print("Enter key: ");
                        key = in.nextInt();
                        break;

            case 2 :    Random rand = new Random();
                        key = rand.nextInt(128);
                        break;
        }

        return key;
    }

    public static String getKeyword() {

        Scanner in = new Scanner(System.in);
        String word = "";

        System.out.println("Enter keyword (letters only) : ");
        word = in.nextLine();

        return word;
    }

    /**
     * Will convert a passed integer into its corresponding ASCII value.
     * 
     * @param num   Integer to convert
     * @return      ASCII value of passed integer
     */
    public static char getAscii(int num) {

        return (char)num;
    }

    /**
     * Will return a random integer between 0 and an upper bound (exclusive).
     * 
     * @param bound Upper bound of random number generator
     * @return      Random integer between [0, bound)
     */
    public static int getRand(int bound) {

        Random rand = new Random();
        int num = rand.nextInt(bound);

        
        return num;
    }

    /**
     * Will search through arraylist to check for an existent value.
     * 
     * @param n     needle
     * @param vals  list of values to search
     * @return      true if value exists, false otherwise
     */
    public static boolean exists(int n, ArrayList<Integer> vals) {

        boolean exist = false;

        for (int i = 0; i < vals.size(); ++i) {

            if (n == vals.get(i)) {

                exist = true;
            }
        }

        return exist;
    }

    public static void print(String text) {

        System.out.println(text);
    }

    public static String parseString(String text) {

        return (text.replaceAll("\\s+", "")).toUpperCase();
    }

    public static String getMirror(String plain, String keyword) {

        String mirror = "";
        int curr = 0;

        for (char c : plain.toCharArray()) {

            if (curr == keyword.length()) {

                curr = 0;
            } 

            mirror += keyword.charAt(curr);
            curr++;

        }
        return mirror.toUpperCase();
    }

    /**
     * Will prompt user with menu to select which encryption scheme to execute.
     * 
     * @return  User's menu selection
     */
    public static int menu() {

        Scanner in = new Scanner(System.in);
        int opt;

        System.out.println("Chhose encryption method :\n");
        System.out.println("1) Ceasar\n2) Homophonic\n3) Vigenere\n4) Affine\n5) Playfair\n6) Hill\n");
        opt = in.nextInt();

        return opt;
    }

    public static int gcd(int a, int b) {

        while (b != 0) {

            int temp = b;
            b = a % b;
            a = temp;
        }
        
        return a;
    }

    public static boolean isCoPrime(int a, int b) {
        
        return gcd(a, b) == 1;
    }
}
