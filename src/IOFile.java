import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class IOFile {
    private final String dataFilePath;
    private final List<Particle> particles= new LinkedList<>();

    public IOFile(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public List<Particle> readDataFile() throws FileNotFoundException {
        File dataFile = new File(dataFilePath);
        Scanner scanner = new Scanner(dataFile);
        try {
            scanner = new Scanner(dataFile);
        } catch (FileNotFoundException e) {
            System.out.println("No such file " + dataFilePath);
        }
        int n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            double rx = scanner.nextDouble();
            double ry = scanner.nextDouble();
            double vx = scanner.nextDouble();
            double vy = scanner.nextDouble();
            double mass = scanner.nextDouble();
            double radius = scanner.nextDouble();
            int color_red = scanner.nextInt();
            int color_green = scanner.nextInt();
            int color_blue = scanner.nextInt();
            Particle p = new Particle(i + 1, radius, mass, color_red, color_green, color_blue);
            p.setPosition(new Vector(rx, ry));
            p.setVelocity(new Vector(vx, vy));
            particles.add(p);
        }
        scanner.close();
        return particles;
    }

}
