package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Rocket;
import model.RocketFileManager;
import model.RocketInventory;
import util.Validator;
import view.AdminDashboardFrame;

public class RocketController {

    private final AdminDashboardFrame view;
    private final RocketInventory inventory;

    // Save in user home to avoid permission issues
    private final String DATA_FILE_PATH = "data/rocket_inventory.csv";

    public RocketController(AdminDashboardFrame view) {
        this.view = view;
        this.inventory = new RocketInventory();

        // Load persistent data (seed 5 if missing/empty)
        loadRocketsOnStartup();

        // Initial UI fill
        refreshHome();
        refreshTable(inventory.getAllRockets());

        // CRUD listeners
        registerAdminControlListeners();

        // Search/Sort listeners
        registerRocketListListeners();
    }

    // ========================= LOAD ON STARTUP =========================

    private void loadRocketsOnStartup() {
        try {
            ArrayList<Rocket> loaded = RocketFileManager.loadFromFile(DATA_FILE_PATH);

            if (loaded.isEmpty()) {
                loaded = seedFiveDefaultRockets();
                RocketFileManager.saveToFile(DATA_FILE_PATH, loaded);
            }

            inventory.setAllRockets(loaded);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    "File loading error (IOException).\nStarting with default 5 rockets.\n\nDetails: " + ex.getMessage(),
                    "File Load Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ArrayList<Rocket> seeded = seedFiveDefaultRockets();
            inventory.setAllRockets(seeded);

            try {
                RocketFileManager.saveToFile(DATA_FILE_PATH, seeded);
            } catch (IOException e2) {
                JOptionPane.showMessageDialog(
                        view,
                        "File saving error (IOException).\nData may not persist.\n\nDetails: " + e2.getMessage(),
                        "File Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    "Data file contains invalid numbers (NumberFormatException).\nStarting with default 5 rockets.",
                    "Corrupt Data File",
                    JOptionPane.ERROR_MESSAGE
            );

            ArrayList<Rocket> seeded = seedFiveDefaultRockets();
            inventory.setAllRockets(seeded);

            try {
                RocketFileManager.saveToFile(DATA_FILE_PATH, seeded);
            } catch (IOException e2) {
                JOptionPane.showMessageDialog(
                        view,
                        "File saving error (IOException).\nData may not persist.\n\nDetails: " + e2.getMessage(),
                        "File Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private ArrayList<Rocket> seedFiveDefaultRockets() {
        ArrayList<Rocket> list = new ArrayList<>();

        // IMPORTANT: Rocket constructor must now include country:
        // Rocket(id, name, country, mass, thrust, status, year)

        list.add(new Rocket("R001", "Falcon 9", "USA", 549.0, 7607.0, "Active", 2010));
        list.add(new Rocket("R002", "Starship", "USA", 5000.0, 15000.0, "Testing", 2020));
        list.add(new Rocket("R003", "Ariane 5", "France", 777.0, 13300.0, "Inactive", 1996));
        list.add(new Rocket("R004", "Electron", "New Zealand", 12.5, 224.0, "Active", 2017));
        list.add(new Rocket("R005", "PSLV", "India", 320.0, 4800.0, "Active", 1993));

        return list;
    }

    private void saveAllRocketsToFile() {
        try {
            RocketFileManager.saveToFile(DATA_FILE_PATH, inventory.getAllRockets());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    "File saving error (IOException).\nYour changes may not persist.\n\nDetails: " + ex.getMessage(),
                    "File Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================= CRUD LISTENERS =========================

    private void registerAdminControlListeners() {
        view.getBtnAddRocket().addActionListener(e -> addRocket());
        view.getBtnUpdateRocket().addActionListener(e -> updateRocket());
        view.getBtnDeleteRocket().addActionListener(e -> deleteRocket());
        view.getBtnClearForm().addActionListener(e -> clearForm());
    }

    // ========================= SEARCH/SORT LISTENERS =========================

    private void registerRocketListListeners() {
        view.getBtnBinarySearch().addActionListener(e -> handleBinarySearch());
        view.getBtnPartialSearch().addActionListener(e -> handlePartialSearch());
        view.getBtnResetSearch().addActionListener(e -> resetRocketList());

        view.getBtnSortAsc().addActionListener(e -> sortAscending());
        view.getBtnSortDesc().addActionListener(e -> sortDescending());
    }

    // ========================= ADD/UPDATE/DELETE =========================

    private void addRocket() {
        try {
            Rocket rocket = readRocketFromForm(true);

            boolean added = inventory.addRocket(rocket);
            if (!added) {
                JOptionPane.showMessageDialog(
                        view,
                        "Duplicate Rocket Name. Rocket already exists.",
                        "Duplicate Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            refreshHome();
            refreshTable(inventory.getAllRockets());
            saveAllRocketsToFile();

            JOptionPane.showMessageDialog(
                    view,
                    "Rocket added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    "Mass, Thrust, and Launch Year must be valid numbers.\nExample: Mass=1000.5, Thrust=5000, Year=2020",
                    "Invalid Number Format",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateRocket() {
        try {
            String nameKey = view.getTxtRocketName().getText().trim();

            String nameError = Validator.validateRocketName(nameKey);
            if (nameError != null) {
                throw new IllegalArgumentException(nameError);
            }

            Rocket existing = inventory.findRocketByName(nameKey);
            if (existing == null) {
                JOptionPane.showMessageDialog(
                        view,
                        "Rocket not found. Enter the correct Rocket Name to update.",
                        "Not Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Rocket updated = readRocketFromForm(false);

            boolean ok = inventory.updateRocket(nameKey, updated);
            if (!ok) {
                JOptionPane.showMessageDialog(
                        view,
                        "Update failed. Rocket not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            refreshHome();
            refreshTable(inventory.getAllRockets());
            saveAllRocketsToFile();

            JOptionPane.showMessageDialog(
                    view,
                    "Rocket updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    "Mass, Thrust, and Launch Year must be valid numbers.",
                    "Invalid Number Format",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteRocket() {
        try {
            String nameKey = view.getTxtRocketName().getText().trim();

            String nameError = Validator.validateRocketName(nameKey);
            if (nameError != null) {
                throw new IllegalArgumentException(nameError);
            }

            int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Are you sure you want to delete rocket: " + nameKey + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            boolean deleted = inventory.deleteRocket(nameKey);
            if (!deleted) {
                JOptionPane.showMessageDialog(
                        view,
                        "Rocket not found. Nothing to delete.",
                        "Not Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            refreshHome();
            refreshTable(inventory.getAllRockets());
            saveAllRocketsToFile();

            JOptionPane.showMessageDialog(
                    view,
                    "Rocket deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {
        view.clearAdminControlForm();
    }

    private Rocket readRocketFromForm(boolean checkDuplicateOnAdd) {
        String id = view.getTxtRocketID().getText().trim();
        String name = view.getTxtRocketName().getText().trim();
        String country = view.getTxtCountry().getText().trim(); // NEW
        String massText = view.getTxtMass().getText().trim();
        String thrustText = view.getTxtThrust().getText().trim();
        String yearText = view.getTxtLaunchYear().getText().trim();

        Object statusObj = view.getCmbStatus().getSelectedItem();
        String status = (statusObj == null) ? "" : statusObj.toString().trim();

        // Basic validations
        String idError = Validator.validateRocketID(id);
        if (idError != null) throw new IllegalArgumentException(idError);

        String nameError = Validator.validateRocketName(name);
        if (nameError != null) throw new IllegalArgumentException(nameError);

        // Country validation (simple, clear)
        if (country.isEmpty()) {
            throw new IllegalArgumentException("Country cannot be empty.");
        }

        String statusError = Validator.validateStatus(status);
        if (statusError != null) throw new IllegalArgumentException(statusError);

        // Duplicate check only for add
        if (checkDuplicateOnAdd && inventory.isDuplicateRocketName(name)) {
            throw new IllegalArgumentException("Duplicate Rocket Name. Rocket already exists.");
        }

        // Numeric validations
        double mass = Validator.parseAndValidateNonNegativeDouble(massText, "Mass");
        double thrust = Validator.parseAndValidateNonNegativeDouble(thrustText, "Thrust");
        int year = Validator.parseAndValidateLaunchYear(yearText);

        // IMPORTANT: Rocket constructor must now include country
        return new Rocket(id, name, country, mass, thrust, status, year);
    }

    // ========================= SEARCH/SORT HANDLERS =========================

    private void handleBinarySearch() {
        String criteria = view.getCmbSearchCriteria().getSelectedItem().toString();
        String input = view.getTxtSearch().getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Search input cannot be empty.", "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (criteria.equalsIgnoreCase("Country")) {
            JOptionPane.showMessageDialog(
                    view,
                    "Binary Search is supported for Rocket Name and Launch Year only.\nUse Partial Search for Country.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if (criteria.equalsIgnoreCase("Rocket Name")) {
            Rocket found = inventory.binarySearchByName(input);

            if (found == null) {
                JOptionPane.showMessageDialog(view, "No rocket found with that Rocket Name.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(inventory.getAllRockets());
            } else {
                ArrayList<Rocket> one = new ArrayList<>();
                one.add(found);
                refreshTable(one);
            }
            return;
        }

        if (criteria.equalsIgnoreCase("Launch Year")) {
            try {
                int year = Integer.parseInt(input);
                Rocket found = inventory.binarySearchByLaunchYear(year);

                if (found == null) {
                    JOptionPane.showMessageDialog(view, "No rocket found with that Launch Year.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable(inventory.getAllRockets());
                } else {
                    ArrayList<Rocket> one = new ArrayList<>();
                    one.add(found);
                    refreshTable(one);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Launch Year must be a number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        JOptionPane.showMessageDialog(view, "Invalid search criteria selection.", "Search Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handlePartialSearch() {
        String criteria = view.getCmbSearchCriteria().getSelectedItem().toString();
        String input = view.getTxtSearch().getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Search input cannot be empty.", "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (criteria.equalsIgnoreCase("Rocket Name")) {
            List<Rocket> results = inventory.partialSearchByName(input);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(view, "No rockets match that name keyword.", "No Results", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(inventory.getAllRockets());
            } else {
                refreshTable(results);
            }
            return;
        }

        if (criteria.equalsIgnoreCase("Country")) {
            List<Rocket> results = inventory.partialSearchByCountry(input);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(view, "No rockets match that country keyword.", "No Results", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(inventory.getAllRockets());
            } else {
                refreshTable(results);
            }
            return;
        }

        if (criteria.equalsIgnoreCase("Launch Year")) {
            List<Rocket> results = inventory.partialSearchByLaunchYear(input);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(view, "No rockets match that year pattern.", "No Results", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(inventory.getAllRockets());
            } else {
                refreshTable(results);
            }
            return;
        }

        JOptionPane.showMessageDialog(view, "Invalid search criteria selection.", "Search Error", JOptionPane.ERROR_MESSAGE);
    }

    private void resetRocketList() {
        view.getTxtSearch().setText("");
        refreshTable(inventory.getAllRockets());
    }

    private void sortAscending() {
        List<Rocket> sorted = inventory.sortByLaunchYear(true);
        refreshTable(sorted);
    }

    private void sortDescending() {
        List<Rocket> sorted = inventory.sortByLaunchYear(false);
        refreshTable(sorted);
    }

    // ========================= HOME REFRESH =========================

    public void refreshHome() {
        view.getLblTotalRocketsValue().setText(String.valueOf(inventory.getTotalCount()));
        view.getLblRecentCountValue().setText(String.valueOf(inventory.getRecentCount()));
        view.getLblActiveCountValue().setText(String.valueOf(inventory.getActiveCount()));
        view.getLblInactiveCountValue().setText(String.valueOf(inventory.getInactiveCount()));
        view.getLblTestingCountValue().setText(String.valueOf(inventory.getTestingCount()));

        List<Rocket> recent = inventory.getRecentRockets();

        view.getLblRecent1().setText("-");
        view.getLblRecent2().setText("-");
        view.getLblRecent3().setText("-");
        view.getLblRecent4().setText("-");
        view.getLblRecent5().setText("-");

        int labelIndex = 1;
        for (int i = recent.size() - 1; i >= 0 && labelIndex <= 5; i--) {
            Rocket r = recent.get(i);
            String text = r.getRocketName() + " (" + r.getLaunchYear() + ")";
            setRecentLabel(labelIndex, text);
            labelIndex++;
        }
    }

    private void setRecentLabel(int position, String text) {
        switch (position) {
            case 1 -> view.getLblRecent1().setText(text);
            case 2 -> view.getLblRecent2().setText(text);
            case 3 -> view.getLblRecent3().setText(text);
            case 4 -> view.getLblRecent4().setText(text);
            case 5 -> view.getLblRecent5().setText(text);
        }
    }

    // ========================= TABLE REFRESH =========================
    
    public void refreshTable(List<Rocket> rockets) {
        DefaultTableModel model = (DefaultTableModel) view.getTblRockets().getModel();
        model.setRowCount(0);

        for (Rocket r : rockets) {

            model.addRow(new Object[]{
                r.getRocketID(),
                r.getRocketName(),
                r.getCountry(),
                r.getMass(),
                r.getThrust(),
                r.getStatus(),
                r.getLaunchYear()
            });
        }
    }
}
