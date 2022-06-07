import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UrbanDirectory {
    private static final String FILE_NOT_FOUND = "Файл не найден или введен некорректный путь, пожалуйста проверьте правильность пути и повторите попытку";
    private static final String FILE_INCORRECT_FORMAT = "Неверный формат файла, пожалуйста укажите путь к файлам форматов .csv или .xml";


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean isExit = false;
        while (!isExit) {
            System.out.println("\nДля выхода из программы введите Q\nВведите путь файла: ");
            String filePath = scanner.next();
            if(filePath.equalsIgnoreCase("Q")) isExit = true;

            List<AddressObject> addressList = new ArrayList<>();

            //проверка формата файла
            if (filePath.endsWith(".csv")) {
                addressList = getAddressFromCSV(filePath);
            } else if (filePath.endsWith(".xml")) {
                addressList = getAddressFromXML(filePath);
            } else System.out.println(FILE_INCORRECT_FORMAT);

            assert addressList != null;
            int count = 0;
            System.out.println("Файл содержит :" + addressList.size() + " записей");
//            for (AddressObject address : addressList) {
//                System.out.println(address);
//            }

            getInfoFloor(addressList);

        }
    }

    public static List<AddressObject> getAddressFromCSV(String filePath){
        List<AddressObject> addressList = new ArrayList<>();
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream(filePath), "WINDOWS-1251");
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
            in.close();
            reader.close();
        } catch (IOException exception){
            System.out.println(FILE_NOT_FOUND);
        }
        return addressList;
    }

    public static List<AddressObject> getAddressFromXML(String filePath) {
        return null;
    }

    public static void getInfoFloor(List<AddressObject> list) {

        Map<String, Map<Integer, List<AddressObject>>> groupingList = list.stream()
                .collect(
                        Collectors.groupingBy(AddressObject::getCity, Collectors.groupingBy(AddressObject::getFloor))
                );
        groupingList.forEach((k,v) -> {
            String city = k;
            System.out.printf("В городе %s :\n", city);
            for(Map.Entry<Integer, List<AddressObject>> entry : v.entrySet()){
                System.out.printf("- зданий с количеством этажей %d - %d шт.\n", entry.getKey(), entry.getValue().size());
            }
            System.out.println();
        } );
    }
}
