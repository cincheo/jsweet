package source.overload.inheritance;

public abstract class SuperClass {


    private String value;



    public boolean valuerModele() {

        if (value != null) {

            valuerModele(value);

        }

        return true;

    }



    protected void valuerModele(String newValue) {

        System.out.println("SuperClass : " + newValue);

    }

}