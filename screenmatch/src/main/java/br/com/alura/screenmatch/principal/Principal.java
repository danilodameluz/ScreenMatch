package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=f1540ab8";

    public void exibirMenu(){
        System.out.println("Digite o nome da Série: ");
        var nomeSerie = leitura.nextLine();
        var json = consumoAPI.obterDados(ENDERECO+nomeSerie.replace(" ",  "+")+APIKEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println( "Série: " + dadosSerie.titulo());
        System.out.println(dadosSerie);

        List<DadosTemporada> temporadas = new ArrayList<>();
		for (int i=1; i<=dadosSerie.totalTemporadas(); i++){
			json = consumoAPI.obterDados(ENDERECO+nomeSerie.replace(" ", "+")+"&Season="+i+APIKEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);

        //Pegando os títulos dos episódios com laçoes de repetição
//        for (int i=0; i < dadosSerie.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j=0; j < episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//
//            }
//        }

        //Pegando os títulos dos episódios com lambda
        //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\nTop 5 Episódios");
        dadosEpisodios.stream()
                .filter(d -> !d.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        System.out.println("\nLista de Episódios");
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        System.out.println("Série produzida nos anos:" );
        episodios.stream()
                .filter(e -> e.getDataLancamento()!= null)
                .map(e -> e.getDataLancamento().getYear())
                .distinct()
                .forEach(System.out::println);

        System.out.println("\nA partir de que ano deseja ver os episódios: ");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate dataBusca = LocalDate.of(ano,1,1);

        episodios.stream()
                .filter(e -> e.getDataLancamento()!= null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(t -> System.out.println(
                        "Temporada: "+ t.getTemporada() +
                                " - Episódio: " + t.getTitulo() +
                                " - Data de Lançamento: " + t.getDataLancamento()
                ));
    }
}
