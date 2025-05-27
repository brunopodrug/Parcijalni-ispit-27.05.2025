package org.example;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DataSource dataSource = createDataSource();
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Uspjesno spajanje na bazu podatka.");
            Scanner sc = new Scanner(System.in);
            do {
                ispisStrings();
                String input = sc.nextLine();

                if (input.equals("6"))
                    break;

                Statement statement = connection.createStatement();
                switch (input) {
                    case "1" -> {
                        System.out.println("Unesite ime novog polaznika: ");
                        String ime = sc.nextLine();
                        System.out.println("Unesite prezime novogo polaznika: ");
                        String prezime = sc.nextLine();
                        unosNovogPolaznika(ime, prezime);
                        System.out.println("----------------------------");
                    }
                    case "2" -> {
                        System.out.println("Unesite ime novog programa obrazovanja: ");
                        String programObrazovanja = sc.nextLine();
                        System.out.println("Unesite koliko csvet bodova ima program: ");
                        int csvet = Integer.parseInt(sc.nextLine());
                        unosNovogProgramaObrazovanja(programObrazovanja, csvet);
                        System.out.println("----------------------------");
                    }
                    case "3" -> {
                        System.out.println("Unesite ID polaznika kojeg zelite dodati na program obrazovanja: ");
                        int idPolaznik = Integer.parseInt(sc.nextLine());
                        System.out.println("Unesite ID programa obrazovanja u kojeg zelite dodati polaznika: ");
                        int idProgObraz = Integer.parseInt(sc.nextLine());
                        dodavanjePolaznikaUProgramObrazovanja(idPolaznik, idProgObraz);
                        System.out.println("----------------------------");
                    }
                    case "4" -> {
                        System.out.println("Unesite ID polaznika kojeg zelite prebaciti: ");
                        int idPolaznik = Integer.parseInt(sc.nextLine());
                        System.out.println("Unesite ID novog programa obrazovanja: ");
                        int idProgObraz = Integer.parseInt(sc.nextLine());
                        updateProgramObrazovanja(idPolaznik, idProgObraz);
                        System.out.println("----------------------------");
                    }
                    case "5" -> {
                        System.out.println("Unesite ID programa obrazovanja za kojeg zelite ispis svih polaznika: ");
                        int idProgObraz = Integer.parseInt(sc.nextLine());
                        ispisSvihPolaznikaPorgramaObrazovanja(idProgObraz);
                        System.out.println("----------------------------");
                    }
                    default -> {
                        System.out.println("Krivo unesena akcija, pokušajte ponovno.");
                        System.out.println("----------------------------");
                    }
                }
                statement.close();
            }
            while (true);
            sc.close();
        } catch (SQLException e) {
            System.err.println("Greska prilikom spajanja na bazu podataka ");
            e.printStackTrace();
        }
    }

    private static void ispisStrings() {
        System.out.println("Unesite broj za izabranu akciju: ");
        System.out.println("1 – unos novog polaznik");
        System.out.println("2 - unesi novi program obrazovanja");
        System.out.println("3 - upisi polaznika na program obrazovanja");
        System.out.println("4 – prebaci polaznika iz jednog u drugi program obrazovanja");
        System.out.println("5 – za zadani program obrazovanja (tražite korisnika unos Id-a), " +
                "ispišite ime i prezime polaznika, naziv programa obrazovanja i broj CSVET bodova za sve " +
                "polaznike koji su ga upisali.");
        System.out.println("6 - kraj programa");
    }

    private static void unosNovogPolaznika(String ime, String prezime) {
        try (Connection connection = createDataSource().getConnection()) {
            CallableStatement cs = connection.prepareCall("{call DodajPolaznika(?,?)}");
            cs.setString(1, ime);
            cs.setString(2, prezime);
            cs.executeUpdate();
            System.out.println("Novi polaznik dodan u bazu podataka.");
        } catch (SQLException e) {
            System.err.println("Greska prilikom spajanja na bazu podataka ");
            e.printStackTrace();
        }
    }

    private static void unosNovogProgramaObrazovanja(String name, int bodovi) {
        try (Connection connection = createDataSource().getConnection()) {
            CallableStatement cs = connection.prepareCall("{call DodajProgramObrazovanja(?,?)}");
            cs.setString(1, name);
            cs.setInt(2, bodovi);
            cs.executeUpdate();
            System.out.println("Novi program obrazovanja dodan u bazu podataka.");
        } catch (SQLException e) {
            System.err.println("Greska prilikom spajanja na bazu podataka ");
            e.printStackTrace();
        }
    }

    private static void dodavanjePolaznikaUProgramObrazovanja(int idPolaznik, int idProgram) {
        try (Connection connection = createDataSource().getConnection()) {
            CallableStatement cs = connection.prepareCall("{call DodajPolaznikuProgramObrazovanja(?,?)}");
            cs.setInt(1, idPolaznik);
            cs.setInt(2, idProgram);
            cs.executeUpdate();
            System.out.println("Polaznik upisan u program obrazovanja.");
        } catch (SQLException e) {
            System.err.println("Greska prilikom spajanja na bazu podataka ");
            e.printStackTrace();
        }
    }

    private static void updateProgramObrazovanja(int polaznikId, int programId) {
        try (Connection connection = createDataSource().getConnection()) {
            CallableStatement cs = connection.prepareCall("{call PromjeniProgramObrazovanja(?,?)}");
            cs.setInt(1, polaznikId);
            cs.setInt(2, programId);
            cs.executeUpdate();
            System.out.println("Polazniku uspjesno promijenjen program obrazovanja");
        } catch (SQLException e) {
            System.err.println("Greska prilikom spajanja na bazu podataka ");
            e.printStackTrace();
        }
    }

    private static void ispisSvihPolaznikaPorgramaObrazovanja(int programID) {

        try (Connection connection = createDataSource().getConnection()) {
            CallableStatement cs = connection.prepareCall("{call IspisSvihPolaznikaPrograma(?)}");
            cs.setInt(1, programID);
            ResultSet resultSet = cs.executeQuery();

            while (resultSet.next()) {
                System.out.println("Ime: " + resultSet.getString(1));
                System.out.println("Prezime: " + resultSet.getString(2));
            }

            System.out.println("Ispis svih polaznika uspjesno napravljen.");
        } catch (SQLException e) {
            System.err.println("Greska prilikom spajanja na bazu podataka ");
            e.printStackTrace();
        }
    }

    private static DataSource createDataSource() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("localhost");
        //ds.setPortNumber(1433); - Po defaultu uvijek na ovi port se spaja
        ds.setDatabaseName("JavaAdv");
        ds.setUser("sa");
        ds.setPassword("SQL");
        ds.setEncrypt(false);

        return ds;
    }
}