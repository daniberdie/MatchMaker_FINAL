package com.example.matchmaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Globals {
    public static Integer idMatch;
    //Clau: Esport, Valor: Llista id partits d'aquell esport
    public static Map<String,List<Integer>> mapIdMatches;
    //Clau: id partit, Valor: array de informació (Descripció, Lloc, Data, etc...)
    public static Map<Integer,String[]> mapMatchData;
    //Clau: Esport, Valor: array (Partits creats, Partits jugats, Partits actius)
    public static Map<String,Integer[]> mapStatistics;

}
