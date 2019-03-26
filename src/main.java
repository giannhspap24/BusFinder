import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        System.out.print("Enter number of brokers\n>>> ");
        Scanner in = new Scanner(System.in);
        int num = in.nextInt();

        Node node = new Node();
        node.init(num);


    }


}
