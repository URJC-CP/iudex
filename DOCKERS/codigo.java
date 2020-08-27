
import java.util.Scanner;

public class codigo {

    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal window.
        //System.out.println("HOLA, mundo");

        Scanner sc = new Scanner(System.in);

        while(sc.hasNext()){
            int n = sc.nextInt();
            //System.out.println(n*2);
        }
        throw new RuntimeException("ERROR PROVOCADO");
    }
}
