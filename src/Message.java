public class Message {
    public static void info(String info, int newLines) {
        String n = "\n".repeat(newLines<0? 0 : newLines);
        System.out.println("info ==> "+info+n);
    }

    public static void error(String error, int newLines) {
        String n = "\n".repeat(newLines<0? 0 : newLines);
        System.out.println("error ==> "+error+n);
    }
}
