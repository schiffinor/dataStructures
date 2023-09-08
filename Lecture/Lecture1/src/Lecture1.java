public class Lecture1 {

    public static void main(String[] args){
        System.out.println("rjschi24");

        int x; //Declaration
        x = 5; //Instantiation

        int y = x + 3;

        double z = y / 3.0;

        double asd = 5;

        System.out.println(z);

        char c = 'c';
        String str = "someStr";
        System.out.println(str);


        System.out.println(addOne((int) asd));

        System.out.println((int) (-5.5));
    }

    public static int addOne(int x){
        int toReturn = x + 1;
        return toReturn;
    }

    public static double divide(int x, double y){
        return x/y;
    }
}
