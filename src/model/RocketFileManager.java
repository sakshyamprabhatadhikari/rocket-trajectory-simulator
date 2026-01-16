package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RocketFileManager {

    // CSV formatgi:
    // rocketID,rocketName,country,mass,thrust,status,launchYear
    //

    public static ArrayList<Rocket> loadFromFile(String filePath) throws IOException {
        ArrayList<Rocket> list = new ArrayList<>();
        File file = new File(filePath);

        // If file doesn't exist, return empty list (controller will seed)
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split into fields (allow empty fields)
                String[] parts = line.split(",", -1);

                // Accept BOTH old(6) and new(7) formats
                if (parts.length != 6 && parts.length != 7) {
                    // skip invalid line
                    continue;
                }

                String id = parts[0].trim();
                String name = parts[1].trim();

                String country;
                double mass;
                double thrust;
                String status;
                int year;

                if (parts.length == 6) {
                    // Old format: no country
                    country = "Unknown";
                    mass = Double.parseDouble(parts[2].trim());
                    thrust = Double.parseDouble(parts[3].trim());
                    status = parts[4].trim();
                    year = Integer.parseInt(parts[5].trim());
                } else {
                    // New format: includes country
                    country = parts[2].trim();
                    mass = Double.parseDouble(parts[3].trim());
                    thrust = Double.parseDouble(parts[4].trim());
                    status = parts[5].trim();
                    year = Integer.parseInt(parts[6].trim());
                }

                // IMPORTANT: Your Rocket constructor must now be:
                // Rocket(String id, String name, String country, double mass, double thrust, String status, int year)
                Rocket r = new Rocket(id, name, country, mass, thrust, status, year);
                list.add(r);
            }
        }

        return list;
    }

    public static void saveToFile(String filePath, List<Rocket> rockets) throws IOException {
        File file = new File(filePath);

        // Ensure parent directory exists
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Rocket r : rockets) {
                // NEW 7-column format
                String line =
                        r.getRocketID() + "," +
                        r.getRocketName() + "," +
                        r.getCountry() + "," +
                        r.getMass() + "," +
                        r.getThrust() + "," +
                        r.getStatus() + "," +
                        r.getLaunchYear();

                bw.write(line);
                bw.newLine();
            }
        }
    }
}
