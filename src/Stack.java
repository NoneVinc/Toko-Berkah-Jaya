class Stack {
    int max = 5;
    int[] data = new int[max];
    int top = -1;

    boolean isEmpty() {
        return top == -1;
    }

    boolean isFull() {
        return top == max - 1;
    }

    void push(int nilai) {
        if (isFull()) {
            System.out.println("Stack penuh!");
        } else {
            data[++top] = nilai;
            System.out.println("Data masuk: " + nilai);
        }
    }

    void pop() {
        if (isEmpty()) {
            System.out.println("Stack kosong!");
        } else {
            System.out.println("Data keluar: " + data[top--]);
        }
    }

    void peek() {
        if (!isEmpty()) {
            System.out.println("Data teratas: " + data[top]);
        }
    }
}