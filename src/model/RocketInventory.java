package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RocketInventory {

    // Main storage
    private final ArrayList<Rocket> rocketList;

    // Recently added (last 5)
    private final Queue<Rocket> recentQueue;

    public RocketInventory() {
        rocketList = new ArrayList<>();
        recentQueue = new LinkedList<>();
    }

    // ========================= BASIC GETTERS =========================

    public ArrayList<Rocket> getAllRockets() {
        return new ArrayList<>(rocketList);
    }

    public List<Rocket> getRecentRockets() {
        return new ArrayList<>(recentQueue);
    }

    // ========================= DUPLICATE CHECK =========================
    // Equivalent to "no duplicate titles" guideline (we use Rocket Name).
    public boolean isDuplicateRocketName(String rocketName) {
        for (Rocket r : rocketList) {
            if (r.getRocketName().equalsIgnoreCase(rocketName)) {
                return true;
            }
        }
        return false;
    }

    // ========================= CRUD =========================

    public boolean addRocket(Rocket rocket) {
        if (isDuplicateRocketName(rocket.getRocketName())) {
            return false;
        }

        rocketList.add(rocket);

        // Update recent queue (keep last 5)
        recentQueue.add(rocket);
        while (recentQueue.size() > 5) {
            recentQueue.remove(); // removes oldest
        }

        return true;
    }

    public boolean updateRocket(String rocketNameKey, Rocket updatedRocket) {
        int index = findRocketIndexByName(rocketNameKey);
        if (index == -1) return false;

        rocketList.set(index, updatedRocket);

        // Keep recent queue consistent with current list
        rebuildRecentQueue();
        return true;
    }

    public boolean deleteRocket(String rocketNameKey) {
        int index = findRocketIndexByName(rocketNameKey);
        if (index == -1) return false;

        rocketList.remove(index);

        // Keep recent queue consistent after delete
        rebuildRecentQueue();
        return true;
    }

    public Rocket findRocketByName(String rocketName) {
        int idx = findRocketIndexByName(rocketName);
        return (idx == -1) ? null : rocketList.get(idx);
    }

    private int findRocketIndexByName(String rocketName) {
        for (int i = 0; i < rocketList.size(); i++) {
            if (rocketList.get(i).getRocketName().equalsIgnoreCase(rocketName)) {
                return i;
            }
        }
        return -1;
    }

    // ========================= STATS (HOME TAB) =========================

    public int getTotalCount() {
        return rocketList.size();
    }

    public int getRecentCount() {
        return recentQueue.size();
    }

    public int getActiveCount() {
        return countByStatus("Active");
    }

    public int getInactiveCount() {
        return countByStatus("Inactive");
    }

    public int getTestingCount() {
        return countByStatus("Testing");
    }

    private int countByStatus(String status) {
        int count = 0;
        for (Rocket r : rocketList) {
            if (r.getStatus() != null && r.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }

    // ========================= LOAD/RESET ALL (FILE I/O SUPPORT) =========================

    public void setAllRockets(ArrayList<Rocket> rockets) {
        rocketList.clear();
        rocketList.addAll(rockets);
        rebuildRecentQueue();
    }

    private void rebuildRecentQueue() {
        recentQueue.clear();
        int start = Math.max(0, rocketList.size() - 5);
        for (int i = start; i < rocketList.size(); i++) {
            recentQueue.add(rocketList.get(i));
        }
    }

    // ========================= STEP 6: SORT (Insertion Sort by Year) =========================

    public ArrayList<Rocket> sortByLaunchYear(boolean ascending) {
        ArrayList<Rocket> sorted = new ArrayList<>(rocketList);

        for (int i = 1; i < sorted.size(); i++) {
            Rocket key = sorted.get(i);
            int j = i - 1;

            if (ascending) {
                while (j >= 0 && sorted.get(j).getLaunchYear() > key.getLaunchYear()) {
                    sorted.set(j + 1, sorted.get(j));
                    j--;
                }
            } else {
                while (j >= 0 && sorted.get(j).getLaunchYear() < key.getLaunchYear()) {
                    sorted.set(j + 1, sorted.get(j));
                    j--;
                }
            }

            sorted.set(j + 1, key);
        }

        return sorted;
    }

    // ========================= STEP 6: BINARY SEARCH =========================
    // Binary search requires sorted data first.

    // Helper: sort by Rocket Name (Insertion sort, case-insensitive)
    private ArrayList<Rocket> sortByRocketNameAscending() {
        ArrayList<Rocket> sorted = new ArrayList<>(rocketList);

        for (int i = 1; i < sorted.size(); i++) {
            Rocket key = sorted.get(i);
            int j = i - 1;

            while (j >= 0 && sorted.get(j).getRocketName()
                    .compareToIgnoreCase(key.getRocketName()) > 0) {
                sorted.set(j + 1, sorted.get(j));
                j--;
            }

            sorted.set(j + 1, key);
        }

        return sorted;
    }

    public Rocket binarySearchByName(String targetName) {
        if (targetName == null) return null;
        String target = targetName.trim();
        if (target.isEmpty()) return null;

        ArrayList<Rocket> sorted = sortByRocketNameAscending();

        int low = 0;
        int high = sorted.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            String midName = sorted.get(mid).getRocketName();

            int cmp = midName.compareToIgnoreCase(target);

            if (cmp == 0) {
                return sorted.get(mid);
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return null;
    }

    public Rocket binarySearchByLaunchYear(int targetYear) {
        ArrayList<Rocket> sorted = sortByLaunchYear(true);

        int low = 0;
        int high = sorted.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            int midYear = sorted.get(mid).getLaunchYear();

            if (midYear == targetYear) {
                return sorted.get(mid);
            } else if (midYear < targetYear) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return null;
    }

    // ========================= STEP 6: PARTIAL SEARCH (Linear Search) =========================

    public ArrayList<Rocket> partialSearchByName(String keyword) {
        ArrayList<Rocket> results = new ArrayList<>();
        if (keyword == null) return results;

        String key = keyword.trim().toLowerCase();
        if (key.isEmpty()) return results;

        for (Rocket r : rocketList) {
            if (r.getRocketName() != null && r.getRocketName().toLowerCase().contains(key)) {
                results.add(r);
            }
        }

        return results;
    }

    public ArrayList<Rocket> partialSearchByLaunchYear(String yearPart) {
        ArrayList<Rocket> results = new ArrayList<>();
        if (yearPart == null) return results;

        String key = yearPart.trim();
        if (key.isEmpty()) return results;

        for (Rocket r : rocketList) {
            String y = String.valueOf(r.getLaunchYear());
            if (y.contains(key)) {
                results.add(r);
            }
        }

        return results;
    }

    // ========================= NEW: PARTIAL SEARCH BY COUNTRY (Linear Search) =========================
    // This is your "author-equivalent" multiple criteria requirement.

    public ArrayList<Rocket> partialSearchByCountry(String keyword) {
        ArrayList<Rocket> results = new ArrayList<>();
        if (keyword == null) return results;

        String key = keyword.trim().toLowerCase();
        if (key.isEmpty()) return results;

        for (Rocket r : rocketList) {
            if (r.getCountry() != null && r.getCountry().toLowerCase().contains(key)) {
                results.add(r);
            }
        }

        return results;
    }
}
