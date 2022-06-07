import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UrbanDirectory {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        boolean isExit = false;
        while (!isExit) {
            System.out.println("\nДля выхода из программы введите Q\nВведите путь файла: ");
            String filePath = scanner.next();
            if(filePath.equalsIgnoreCase("Q")) isExit = true;

            List<AddressObject> addressList = new ArrayList<>();
            if (filePath.endsWith(".csv")) {
                addressList = getAddressFromCSV(filePath);
            } else if (filePath.endsWith(".xml")) {
                addressList = getAddressFromXML(filePath);
            } else System.out.println("File format is not available, please choose file with format .csv or .xml");
                
            assert addressList != null;
            for (AddressObject address : addressList) {
                System.out.println(address);
            }

        }
    }

    public static List<AddressObject> getAddressFromCSV(String filePath){
        List<AddressObject> addressList = new ArrayList<>();
        try {
            InputStreamReader in = new InputStreamReader(
                    new FileInputStream(filePath), "WINDOWS-1251"
            );
            BufferedReader reader = new BufferedReader(in);

            String str = reader.readLine();
            while ((str = reader.readLine()) != null){
                if (!str.isEmpty()){
                    String[] strings = str.split(";");
                    addressList.add(new AddressObject(
                            strings[0],
                            strings[1],
                            Integer.parseInt(strings[2]),
                            Integer.parseInt(strings[3])
                    ));
                }
            }

        } catch (IOException exception){
            System.out.println("Файл не найден или введен некорректный путь, пожалуйста проверьте правильность пути и повторите попытку");
        }
        return addressList;
    }

    public static List<AddressObject> getAddressFromXML(String filePath) {
        return null;
    }
}
