import java.util.Scanner;

public class Odstredovani {

    public static String vypis;
    public static boolean zavrene_dvere = false;
    public static boolean PU_start = false;
    public static STAV stav = STAV.IDLE;
    public static boolean casovac = false;
    public static boolean PU_cas = false;
    public static boolean PU_hladina = false;
    public static int citac = 3;

    static Scanner sc = new Scanner(System.in);

    public static void main (String[] args) {
        vypis = "Vitejte v pračce." + "\n"
                + "Manual: Pro vstup signalu od cidel pouzijte klavesnici."  + "\n"
                + "Signály prichazi v nasledujicim poradi:" + "\n"
                + "S (start)" + "\n"
                + "Z (zavreni dveri)" + "\n"
                + "V (casovac dosahl pozadovane hodnoty - 4x po sobe)" + "\n"
                + "L (hladina klesla na pozadovane minimum)" + "\n";
        vypisStav(vypis);
        vypis = "Jsem v IDLE.";

        while (true) {
            String input = vratInput(sc);
            zjistiAkci(input);
            stavovy_automat();
            vypisStav(vypis);
        }
    }

    public static void stavovy_automat () {
        switch (stav){
            case IDLE:                      {stav_0();break;}
            case START:                     {stav_1();break;}
            case MEZI_TOCENIM1:             {stav_2();break;}
            case MEZI_TOCENIM2:             {stav_3();break;}
            case VYPOUSTENI_VODY:           {stav_4();break;}
            case UKONCENI_VYPOUSTENI_VODY:  {stav_5();break;}
            default:
                vypis = "Katastroficka chyba.";
                break;
        }
    }

    public static void stav_0 (){
        if (PU_start == true) {
            PU_start = false;
            stav = STAV.START;
            vypis = "Jsem ve STARTU.";
        }
        PU_hladina = false;
        PU_cas = false;
    }

    /**
     * zde menim stavy a zaroven provadim akce
     */
    public static void stav_1 (){
        // jsou zavrene dvere?
        if (zavrene_dvere == false) {
            System.out.println ("Prosim zavrete dvere.");
        } else {
            // ZACNU TOCIT MOTOREM NA JEDNU STRANU
            stav = STAV.MEZI_TOCENIM1;
            vypis = "Jsme v MEZI_TOCENIM1";
            // spustim casovac
            casovac = true;
            System.out.println("Spoustim casovac.");
            System.out.println("Zacinam tocit motorem po smeru hodinovych rucicek.");
        }
        // preventivne nastavim priznak startu na false, protoze kdyz by honekdo behem teto casti cyklu zmackl
        // zustalo by to tam zavazet a az budu tento priznak opravu potrebovat, mohl by byt nastaven jinak, nez
        // by mel byt (diky tomuto stisknuti tlacitka)
        PU_start = false;
        PU_hladina = false;
        PU_cas = false;
    }

    public static void stav_2 (){
        if (PU_cas == true){
            System.out.println("Casovac PRY dosahl pozadovane hodnoty. Zastavim toceni motoru.");
            stav = STAV.MEZI_TOCENIM2;
            vypis = "Jsme v MEZI_TOCENIM2.";
            System.out.println("Zacinam tocit mototrem proti smeru hodinovych rucicek");
            PU_cas = false;
            citac -=1;
//            PU_start = false;
        }
    }

    public static void stav_3 (){
        if(PU_cas == true) {
            System.out.println("Casovac dosahl pozadovaene hodnoty po druhe. Zastavim toceni motoru proti smeru hodinovych rucicek.");
            // kolikaty cyklus to je?
            if (citac != 0) {
                stav = STAV.MEZI_TOCENIM1;
                System.out.println("Zacinam tocit motorem po smeru hodinovych rucicek.");
                vypis = "Jsme v MEZI_TOCENIM1.";
                PU_cas = false;
                citac -= 1;
                //            PU_start = false;
            } else {
                // treti cyklus zdimani za nami -> prechod na vypousteni
                stav = STAV.VYPOUSTENI_VODY;
                vypis = "Jsem ve VYPOUSTENI_VODY.";
                System.out.println("Ukoncuji zdimani.");
                PU_cas = false;
                // vypnu casovac
                casovac = false;
            }
        }
    }

    public static void stav_4 (){
        System.out.println("Oteviram vypousteci ventil.");
        System.out.println("Spoustim cerpadlo.");
        stav = STAV.UKONCENI_VYPOUSTENI_VODY;
        vypis = "Jsem v UKONCENI_VYPOUSTENI_VODY.";
        PU_start = false;
        PU_cas = false;
    }

    public static void stav_5 () {
        if (PU_hladina == true) {
            System.out.println ("Vypinam cerpadlo.");
            System.out.println ("Zaviram vypousteci ventil.");
            PU_hladina = false;
            stav = STAV.IDLE;
            PU_start = false;
            PU_cas = false;
            vypis = "Jsem v IDLE.";
        }
    }


    /**
     * Prevadim jednopismenkovy vstup z klavesnice na signal od cidla a vypisuji ho.
     * Pripadne nastavuji priznaky udalosti.
     * @param akce
     */
    public static void zjistiAkci(String akce) {
        if (akce.length() > 0) {
            switch (Character.toUpperCase(akce.charAt(0))) {
                // dvere jsou zavrene
                case 'S': {
                    PU_start = true;
                    System.out.println("Stiskli jsme start.");
                    break;
                }
                // dvere zavrene / otevrene
                case 'Z': {
                    if (zavrene_dvere == false) {
                        zavrene_dvere = true;
                        System.out.println("Dvere jsme prave zavreli.");
                    } else {
                        zavrene_dvere = false;
                        System.out.println("Dvere jsme prave otevreli.");
                    }
                    break;
                }

                // casovac dosahl pozadovane hodnoty
                case 'V': {
                    PU_cas = true;
                    System.out.println("Casovac dosahl pozadovane hodnoty.");
                    break;
                }

                // hladina dosahla minimalni hodnoty
                case 'L': {
                    PU_hladina = true;
                    System.out.println("Hladina vody je pod minimalni pozadovanou hodnotou.");
                    break;
                }

                default: {
                    //vypis = "Neplatne zadani.";
                    System.out.println("Neplatne zadani.");
                }
            }
        }
    }

    // TODO: KDYKOLIV UZIVATEL MUZE PRERUSIT PRACI CYKLUS (AT UZ TLACITKEM START NEBO OTEVRENIM DVERI)

    /**
     * metoda pro vypis
     * @param vypis string, ktery ma byt vypsan
     */
    public static void vypisStav (String vypis){
        System.out.println (vypis);
    }

    /**
     * vraci vstup z klavesnice
     * @param sc
     * @return
     */
    public static String vratInput(Scanner sc) {
        String akce = sc.nextLine();
        return akce;
    }
}
