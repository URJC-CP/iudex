import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

class Scratch {
    public static void main(String[] args) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        String[] parts = reader.readLine().split(" ");
        int n = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);

        var set = new DisjointSet(n);

        for (int i = 0; i < m; i++) {
            parts = reader.readLine().split(" ");
            int part1 = Integer.parseInt(parts[1]);
            switch (parts[0]){
                case "CONTACTO":
                    int part2 = Integer.parseInt(parts[2]);
                    set.union(part1, part2);
                    break;
                case "POSITIVO":
                    set.markInfected(part1);
                    break;

                case "?":
                    System.out.println(set.isInfected(part1)? "POSIBLE": "NO");
                    break;
            }
        }
    }

    public static class DisjointSet {

        private final int[] parent;
        private final int[] size;
        private final boolean[] infected;

        public DisjointSet(int n) {
            int arraysize = n + 1; // 0 unused
            parent = new int[arraysize];
            for (int i = 0; i < parent.length; i++) {
                parent[i] = i;
            }
            size = new int[arraysize];
            Arrays.fill(size,1);
            infected = new boolean[arraysize];
        }

        public int size(int n){
            return size[find(n)];
        }

        public int find(int n){
            if(parent[n] != n){
                parent[n] = find(parent[n]);
            }
            return parent[n];
        }

        public boolean union(int a, int b){
            int rootA = find(a);
            int rootB = find(b);

            if(rootA == rootB) return false;

            int sizeA = size(rootA);
            int sizeB = size(rootB);

            if(sizeA < sizeB){ //SWAP
                int t = rootA;
                rootA = rootB;
                rootB = t;
            }

            parent[rootB] = rootA;
            size[rootA] += size[rootB];
            infected[rootA] |= infected[rootB];
            return true;
        }



        // Specific methods for this problem
        public void markInfected(int a){
            infected[find(a)] = true;
        }

        public boolean isInfected(int a){
            return infected[find(a)];
        }
    }
}