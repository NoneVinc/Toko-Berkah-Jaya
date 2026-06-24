public class Main {
    public static void main(String[] args) {
        Stack s = new Stack();

        s.push(10);
        s.push(20);
        s.push(30);

        s.peek(); // lihat atas

        s.pop();  // hapus atas
        s.pop();

        s.peek();
    }
}