/*
 * Esse programa tem como propósito ler o conteúdo de um arquivo .csv 
 * (modelo: xx;yy;zz) que contenha informações de continente, país, cidade,
 * latitude, longitude e população da cidade de localizações.
 * 
 * Sua saída consiste nas duas cidades das localizações cuja distância é a maior
 * dentre todas da lista dentro do contexto do globo terrestre.
 *
 * Modo de uso:
 *  java Distancias < Dados.csv
 *  
 * Opções / Flags:
 *  C - Específica que apenas as linhas de tal continente devem 
 *      ser avaliadas. String*
 *  P - Específica que apenas as linhas de tal país devem ser avaliadas. String*
 *  + - Específica que apenas as linhas cuja cidade tem população maior do que
 *      um valor inteiro específico devem ser avaliadas. (int)
 *  - - Específica que apenas as linhas cuja cidade tem população menor do que 
 *      um valor inteiro específico devem ser avaliadas. (int) 
 *
 * Modo de uso com flags:
 *  java Distancias P Brazil < Dados.csv 
 */

import java.io.*;
import java.util.ArrayList;

public class Distancias {
  public static void main(String[] args) throws Exception {

    // Validação das flags
    boolean checkingContinent = false;
    String paramContinent = null;

    boolean checkingCountry = false;
    String paramCountry = null;

    boolean checkingMoreThanPop = false;
    int paramMoreThanPop = 0;

    boolean checkingLessThanPop = false;
    int paramLessThanPop = 0;


    for (int i = 0; i < args.length; i++) {
      String flag = args[i];

      if (i + 1 < args.length) {
        String param = args[i + 1];

        switch (flag.toUpperCase()) {
          case "C":
            checkingContinent = true;
            paramContinent = param;
            break;
          case "P":
            checkingCountry = true;
            paramCountry = param;
            break;
          case "+":
            checkingMoreThanPop = true;
            paramMoreThanPop = Integer.parseInt(param);
            break;
          case "-":
            checkingLessThanPop = true;
            paramLessThanPop = Integer.parseInt(param);
            break;
          default:
            System.out.printf("[ERRO] Flag desconhecida (%s)\n", flag);
            break;
        }
        i++; 
      } else {
        System.out.printf("[ERRO] Flag sem parâmetro. (%s)\n", flag);
      }
    }

    // Leitura do CSV e classificação das cidades.
    // Ajustando BufferedReader para ler o conteúdo do CSV
    ArrayList<String> continents = new ArrayList<>();
    ArrayList<String> countries = new ArrayList<>();
    ArrayList<String> cities = new ArrayList<>();
    ArrayList<Double> lats = new ArrayList<>();
    ArrayList<Double> lons = new ArrayList<>();
    ArrayList<Integer> pops = new ArrayList<>();

    BufferedReader br = new BufferedReader(
        new InputStreamReader(System.in)
        );

    // Início da validação dos dados
    String inputLine;
    while ((inputLine = br.readLine()) != null) {
      String[] columns = inputLine.split(";");
      if(columns.length != 6) continue;

      String continent = columns[0];
      String country = columns[1];
      String city = columns[2];
      Double lat = Double.parseDouble(columns[3]);
      Double lon = Double.parseDouble(columns[4]);
      int pop = Integer.parseInt(columns[5]);

      // Aplicação dos filtros definidos no início da execução.
      if (checkingContinent && !continent.equalsIgnoreCase(paramContinent))
        continue;
      if (checkingCountry && !country.equalsIgnoreCase(paramCountry))
        continue;
      if (checkingMoreThanPop && pop < paramMoreThanPop)
        continue;
      if (checkingLessThanPop && pop > paramLessThanPop)
        continue;

      continents.add(continent);
      countries.add(country);
      cities.add(city);
      lats.add(lat);
      lons.add(lon);
      pops.add(pop);
    }

    // Calculando as distâncias
    double maiorDistancia = 0;      // Maior distancia atual, quando falamos
                                    // no contexto de execução do algoritmo.

    int indexCity1 = -1;            // O valor de -1 representa algo impossível
    int indexCity2 = -1;            // para um índice de array, quer dizer que
                                    // não sobrou cidades ou havia menos de 2.

    for (int i = 0; i < cities.size(); i++) {
      for (int j = i+1; j < cities.size(); j++) {
        double dist = distanciaViaHaverseno(
            lats.get(i),  lons.get(i),
            lats.get(j),  lons.get(j)
            );
        if (dist > maiorDistancia) {
          maiorDistancia = dist;
          indexCity1 = i;
          indexCity2 = j;
        }
      }
    }

    // Validação final
    if (indexCity1 != -1) {
      System.out.printf(
          "Maior distância: %s (%s) <-> %s (%s) = %.2f km%n",
          cities.get(indexCity1), countries.get(indexCity1),
          cities.get(indexCity2), countries.get(indexCity2),
          maiorDistancia
          );
    } else {
      System.out.println(
      "[ERRO] Nenhuma cidade encontrada após aplicação dos filtros."
      );
    }
  }

  // Equação de Haverseno para calcular a distância entre dois pontos no globo.
  public static double distanciaViaHaverseno(double lat1, double lon1,
                                            double lat2, double lon2) {

    final double RAIO_TERRESTRE = 6378.13;  // em KM
    double lat1Rad = Math.toRadians(lat1);  // Convertendo pra radianos
    double lon1Rad = Math.toRadians(lon1);
    double lat2Rad = Math.toRadians(lat2);
    double lon2Rad = Math.toRadians(lon2);

    return (2 * RAIO_TERRESTRE) * Math.asin(Math.sqrt(
          Math.pow(Math.sin((lat2Rad - lat1Rad)/2), 2)
          + Math.cos(lat1Rad) *  Math.cos(lat2Rad)
          * Math.pow(Math.sin((lon2Rad - lon1Rad)/2), 2)
          ));
  }
}