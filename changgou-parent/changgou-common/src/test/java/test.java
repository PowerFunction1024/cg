import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

public class test {
    public static void main(String[] args) throws ParseException {

        Scanner scanner = new Scanner(System.in);
        String date = scanner.nextLine();

        if (date.length()>6){
            String year = date.substring(0, 4);//年
            String mouth = date.substring(4, 6);//月
            String day = date.substring(6, 8);//日


            //转为了数字
            Integer year1 = Integer.valueOf(year);
            Integer mouth1 = Integer.valueOf(mouth);
            Integer day1 = Integer.valueOf(day);

            //再去判断大小,比如月份不要大于12,等等


            //再去拼接,年月日
            System.out.println(year1+"年"+mouth1+"月"+day1+"日");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-hh");
            Date parse = simpleDateFormat.parse("2019-02-04");


            System.out.println(year);
            System.out.println(mouth);
            System.out.println(day);



        }





    }
}
