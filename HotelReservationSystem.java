
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "root";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("Choose an option");
                System.out.println("  HOTEL MANAGEMENT SYSTEM ");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve room");
                System.out.println("2. view Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(con, sc);
                        break;

                    case 2:
                        view_reservation(con);
                        break;

                    case 3:
                        Get_Room_Number(con, sc);
                        break;

                    case 4:
                        Update_reservation(con, sc);
                        break;

                    case 5:
                        Delete_reservation(con, sc);
                        break;

                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid option,try again");
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    private static void reserveRoom(Connection con, Scanner sc) {
        try {
            System.out.println("Enter guest name");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("enter room number");
            int roomNumber = sc.nextInt();
            System.out.println("enter contact Number");
            String contactNumber = sc.next();
            String sql = "INSERT INTO reservations (guest_name,room_number,contact_number)" + "VALUES('" + guestName + "'," + roomNumber + ",'" + contactNumber + "')";
            try (Statement statement = con.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservations Successfully");
                } else {
                    System.out.println("Reservation False");
                }
            }
        }catch (SQLException e) {
                e.printStackTrace();

        }
    }


    private static void view_reservation(Connection con) throws SQLException {
        {
            String sql = "Select reservation_id ,guest_name, room_number,contact_number,reservation_date From reservation";
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                System.out.println("Current reservations :");
                System.out.println("+------------------+----------------+---------------+----------------+-------------------+");
                System.out.println("| reservation_id   | guest_name      |room_number      |contact_number |  reservation_date");
                System.out.println("+------------------+-----------------+---------------+----------------+-------------------+");

                while (resultSet.next()) {
                    int reservation_id = resultSet.getInt("reservation_id");
                    String guest_name = resultSet.getString("guest_name");
                    int room_number = resultSet.getInt("room_number");
                    String contact_number = resultSet.getString("contact_number");
                    String reservation_date = resultSet.getTimestamp("reservation_date").toString();
                    System.out.printf("| %-14d | %-15s| %-13d| %-20s| %-19s|\n", reservation_id, guest_name, room_number, contact_number, reservation_date);


                }
                System.out.println("+----------------+-------------------+----------------+-------------------+------------------+");
            }

        }
    }
    private  static void  Get_Room_Number(Connection con ,Scanner sc){
        try {
            System.out.println("Enter Reservation id :");
            int resrvationId=sc.nextInt();
            System.out.println("Enter Guest Name :");
            String guestName =sc.next();
            String sql ="SELECT room_number FROM reservation"+"WHERE reservation_id="+resrvationId+"And guest_name='"+guestName+"'";
            try(Statement statement=con.createStatement();
            ResultSet resultSet=statement.executeQuery(sql)){
                if(resultSet.next()){
                    int roomNumber =resultSet.getInt("room_number");
                    System.out.println("Room number for reservation Id"+resrvationId+"and Guest "+guestName+"is: "+roomNumber);
                }
                else {
                    System.out.println("Reservation not found for this name ");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    private static void Update_reservation(Connection con, Scanner sc) {
        try {
            System.out.println("enter reservation id to update");
            int reservationid = sc.nextInt();
            sc.nextLine();
            if (!reservationExists(con, reservationid)) {
                System.out.println("reservation not found for the id");
                return;
            }
            System.out.println("enter new guest name");
            String newGuestName = sc.nextLine();
            System.out.println("Enter a new room number");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter a new ContactNumber");
            String newContactnumber = sc.next();
            String sql = "update reservations set guest_name ='" + newGuestName + "'," +
                    "room_number= " + newRoomNumber + "," +
                    "contact_number ='" + newContactnumber + "'" +
                    "WHERE reservation_id=" + reservationid;

            try (Statement statement = con.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservations Successfully");
                } else {
                    System.out.println("Reservation False");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void Delete_reservation(Connection con, Scanner sc) {
        try {
            System.out.println("enter reservation ID to delete : ");
            int reservationID = sc.nextInt();
            if (!reservationExists(con, reservationID)) {
                System.out.println("reservation not founded for the given ID ");
                return;
            }
            String sql = "DELETE FROM reservation_id =" + reservationID;
            try(Statement statement=con.createStatement()) {

                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservation delete successfully");
                } else {
                    System.out.println("reservation failed");
                }
            }
        } catch (SQLException e) {
                e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection con, int reservationId) {
        try {
            String sql = "SELECT reservation_id from reservation WHERE reservation_id =" + reservationId;
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


         public static void exit ()throws InterruptedException {
             System.out.print("Exiting System");
             int i = 5;
             while (i != 0) {
                 System.out.print(".");
                 Thread.sleep(450);
                 i--;
             }
             System.out.println();
             System.out.println("thank you for using hotel Reservation System!!!");
         }

}



