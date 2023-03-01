import java.awt.*;
import java.awt.event.*;
import java.util.PriorityQueue;
import javax.swing.*;
import java.lang.Math;
import java.util.Comparator;

public class PathFinder extends JFrame implements MouseMotionListener, MouseListener {
    Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    int[][] graph;
    int[][] cost;
    int[][] parent;
    boolean set_start = false;

    JTextField text;
    Font font;
    private JPanel panel;
    private JFrame frame;
    private JButton button;

    int[] goal = new int[2];
    int[] start = new int[2];



    PathFinder(){

        graph = new int[(int) screen_size.getHeight()][(int) screen_size.getWidth()];
        cost = new int[(int) screen_size.getHeight()][(int) screen_size.getWidth()];
        parent = new int[(int) screen_size.getHeight()][(int) screen_size.getWidth()];

        frame = new JFrame("A Project");
        panel = new JPanel();

        text = new JTextField("Set the start point of the agent");
        text.setEditable(false);
        text.setBackground(Color.WHITE);

        font  = new Font("Arial", Font.BOLD, 20);
        text.setBounds((int) screen_size.getWidth() / 2 - 200, 0, 300, 50);
        text.setFont(font);
        text.setBorder(null);

        panel.add(text);
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);

        frame.setSize((int) screen_size.getWidth(), (int) screen_size.getHeight());
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setLayout(null);

        frame.add(panel);

        for (int i = 0; i < (int) screen_size.getHeight(); i++) {
            for (int j  = 0; j < (int) screen_size.getWidth(); j++)
            {
                graph[i][j] = 0;
            }
        }

        for (int i = 0; i < (int) screen_size.getHeight(); i++) {
            for (int j  = 0; j < (int) screen_size.getWidth(); j++)
            {
                cost[i][j] = 2147483647;
            }
        }

        for (int i = 0; i < (int) screen_size.getHeight(); i++) {
            for (int j  = 0; j < (int) screen_size.getWidth(); j++)
            {
                parent[i][j] = -1;
            }
        }

        panel.addMouseListener(this);

    }

    public void mouseClicked(MouseEvent e) {
        Graphics graphic = panel.getGraphics();
        if (!set_start) {
            start[0] = e.getX();
            start[1] = e.getY();

            graphic.setColor(Color.BLUE);
            graphic.fillRect(start[0], start[1],20,20);

            text.setText("Set the goal of the agent");
            set_start = true;

        } else {
            goal[0] = e.getX();
            goal[1] = e.getY();

            graphic.setColor(Color.GREEN);
            graphic.fillRect(goal[0], goal[1],20,20);
            text.setText("Draw the walls");

            button = new JButton("Start Navigation");
            button.setBounds((int) screen_size.getWidth() / 2 - 100, (int) screen_size.getHeight() - 150, 200, 50);
            button.setBackground(Color.YELLOW);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    AStar(start[0], start[1], goal[0], goal[1]);
                }
            });
            panel.add(button);
            button.repaint();

            panel.removeMouseListener(this);
            panel.addMouseMotionListener(this);
        }

    };
    public void mouseEntered(MouseEvent e) {};
    public void mouseExited(MouseEvent e) {};
    public void mousePressed(MouseEvent e) {};
    public void mouseReleased(MouseEvent e) {};

    public void mouseDragged(MouseEvent e) {
        Graphics graphic = panel.getGraphics();
        graphic.setColor(Color.RED);
        int X = e.getX(), Y = e.getY();
        graphic.fillRect(X, Y,20,20);
        for (int i = Y; i < Y + 20; i++) {
            for (int j = X; j < X + 20; j++) {
                graph[i][j] = 1;
            }
        }
    }

    public void move(int oldX, int oldY, int newX, int newY, boolean color) {
        Graphics graphic = panel.getGraphics();
        if (color == false) {
            graphic.setColor(Color.BLUE);
        } else {
            graphic.setColor(Color.YELLOW);
        }
        graphic.fillRect(newX, newY,20,20);
    }

    public void AStar(int startX, int startY, int goalX, int goalY) {
        panel.removeMouseMotionListener(this);
        PriorityQueue<int[]> pq = new PriorityQueue<int[]>(new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                return a[0] - b[0];
            }
        });
        int[][] visited = new int[(int) screen_size.getHeight()][(int) screen_size.getWidth()];

        for (int i = 0; i < (int) screen_size.getHeight(); i++) {
            for (int j  = 0; j < (int) screen_size.getWidth(); j++)
            {
                visited[i][j] = 0;
            }
        }
        pq.add(new int[]{Math.abs(goalX - startX) + Math.abs(goalY - startY), startX, startY});
        visited[startY][startX] = 1;
        cost[startY][startX] = 0;
        int oldX = startX, oldY = startY;

        while (!pq.isEmpty()) {
            int[] arr = pq.poll();

            move(oldX, oldY, arr[1], arr[2], false);

            if (arr[1] == goalX && arr[2] == goalY) {
                int a = arr[1], b = arr[2];
                int[][] path = new int[(int) screen_size.getWidth() * (int) screen_size.getHeight()][2];
                int counter = 0;
                while (a != startX || b != startY) {
                    if (parent[b][a] == 1) {
                        a += 1;
                    }
                    else if (parent[b][a] == 3) {
                        a -= 1;
                    }
                    else if (parent[b][a] == 4) {
                        b += 1;
                    }
                    else if (parent[b][a] == 2) {
                        b -= 1;
                    }
                    path[counter] = new int[]{a, b};
                    counter++;
                }
                for (int i = counter - 1; i >= 0; i--) {
                    move(0, 0, path[i][0], path[i][1], true);
                }
                return;
            }

            if (arr[1] > 0 && arr[1] + 1 < screen_size.getWidth() && arr[2] > 0 && arr[2] < screen_size.getHeight() && graph[arr[2]][arr[1] + 1] == 0 && visited[arr[2]][arr[1] + 1] == 0) {
                visited[arr[2]][arr[1] + 1] = 1;
                if (cost[arr[2]][arr[1]] + 1 < cost[arr[2]][arr[1] + 1]) {
                    cost[arr[2]][arr[1] + 1] = cost[arr[2]][arr[1]] + 1;
                    parent[arr[2]][arr[1] + 1] = 3;
                    pq.add(new int[]{cost[arr[2]][arr[1] + 1] + Math.abs(goalX - (arr[1] +1)) + Math.abs(goalY - arr[2]), arr[1] + 1, arr[2]});
                }
            }
            if (arr[1] - 1 > 0 && arr[1] - 1 < screen_size.getWidth() && arr[2] > 0 && arr[2] < screen_size.getHeight() && graph[arr[2]][arr[1] - 1] == 0 && visited[arr[2]][arr[1] - 1] == 0) {
                visited[arr[2]][arr[1] - 1] = 1;
                if (cost[arr[2]][arr[1]] + 1 < cost[arr[2]][arr[1] - 1]) {
                    cost[arr[2]][arr[1] - 1] = cost[arr[2]][arr[1]] + 1;
                    parent[arr[2]][arr[1] - 1] = 1;
                    pq.add(new int[]{cost[arr[2]][arr[1] - 1] + Math.abs(goalX - (arr[1] - 1)) + Math.abs(goalY - arr[2]), arr[1] - 1, arr[2]});
                }
            }
            if (arr[2] + 1  > 0 && arr[2] + 1 < screen_size.getHeight() && arr[1] > 0 && arr[1] < screen_size.getWidth() && graph[arr[2] + 1][arr[1]] == 0 && visited[arr[2] + 1][arr[1]] == 0) {
                visited[arr[2] + 1][arr[1]] = 1;
                if (cost[arr[2]][arr[1]] + 1 < cost[arr[2] + 1][arr[1]]) {
                    cost[arr[2] + 1][arr[1]] = cost[arr[2]][arr[1]] + 1;
                    parent[arr[2] + 1][arr[1]] = 2;
                    pq.add(new int[]{cost[arr[2] + 1][arr[1]] + Math.abs(goalX - (arr[1])) + Math.abs(goalY - (arr[2] + 1)), arr[1], arr[2] + 1});
                }
            }
            if (arr[1] > 0 && arr[1] < screen_size.getWidth() && arr[2] - 1 > 0 && arr[2] - 1 < screen_size.getHeight() && graph[arr[2] - 1][arr[1]] == 0 && visited[arr[2] - 1][arr[1]] == 0) {
                visited[arr[2] - 1][arr[1]] = 1;
                if (cost[arr[2]][arr[1]] + 1 < cost[arr[2] - 1][arr[1]]) {
                    cost[arr[2] - 1][arr[1]] = cost[arr[2]][arr[1]] + 1;
                    parent[arr[2] - 1][arr[1]] = 4;
                    pq.add(new int[]{cost[arr[2] - 1][arr[1]] + Math.abs(goalX - (arr[1])) + Math.abs(goalY - (arr[2] - 1)), arr[1], (arr[2] - 1)});
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e) {}

    public static void main(String[] args) {

        PathFinder frame = new PathFinder();

    }
}  