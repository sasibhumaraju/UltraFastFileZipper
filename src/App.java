import java.util.Scanner;

public class App {
   
    public static void main(String[] args) throws Exception {
        Message.info("Welcome to UltraZipper", 0);
        Scanner sc = new Scanner(System.in);

        while(true) {
            Message.info("Please Select Options (1-3)", 1);
            Message.info("1) File Zipper", 0);
            Message.info("2) File UnZipper", 0);
            Message.info("3) Exit", 1);
            int i = sc.nextInt();

            switch (i) {
                case 1: FileZipper.compress(); break;
                case 2: FileZipper.decompress(); break;
                case 3: break;
                default: Message.error("Please provide valid input", 1);
            
            }


        }
    }
}
