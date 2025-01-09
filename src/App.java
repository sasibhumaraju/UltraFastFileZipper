import java.util.Scanner;

public class App {
   
    public static void main(String[] args) throws Exception {
        Message.info("Welcome to UltraZipper", 0);

        while(true) {
            Message.info("Please Select Options (1-4)", 1);
            Message.info("1) File Zipper", 0);
            Message.info("2) File UnZipper", 0);
            Message.info("3) List All Zipped Files", 0);
            Message.info("4) Exit", 1);
            Scanner sc = new Scanner(System.in);
            try {
                int i = sc.nextInt();
                if(i==4) break;
                switch (i) {
                    case 1: FileZipper.compress(); break;
                    case 2: FileZipper.decompress(); break;
                    case 3: FileZipper.showAllZippedFiles(); break;
                    default: Message.error("Please provide valid input", 1);
                }
            } catch (Exception e) {
                Message.error("Please provide input in numbers only (1-4)", 1);
            }
        }
        sc.close();
        Message.info("Thank you. Have a Good Day", 2);

    }
}
