package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=f1540ab8";
    private List<DadosSerie> listaSeriesBuscadas = new ArrayList<>();
    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibirMenu(){
        int opcao = -1;
        while (opcao != 0) {
            var menu = """
                    *** OPÇÕES ***
                    1 - SÉRIES
                    2 - EPISÓDIOS
                    3 - SÉRIES BUSCADAS
                                    
                    0 - SAIR                
                    """;

            System.out.println(menu);
            System.out.println("Digite a opção desejada:");
            opcao = leitura.nextInt();
            leitura.nextLine();
            switch (opcao) {
                case 1:
                    buscarSerieNaWeb();
                    break;
                case 2:
                    buscarEpisodiosPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 0:
                    System.out.println("Saindo ... ");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }

        //*** Pegando os títulos dos episódios com laços de repetição
//        for (int i=0; i < dadosSerie.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j=0; j < episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//
//            }
//        }

        //*** Pegando os títulos dos episódios com lambda
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());

//        System.out.println("\nTop 5 Episódios");
//        dadosEpisodios.stream()
//                .filter(d -> !d.avaliacao().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .limit(5)
//                .forEach(System.out::println);

        //*** Mostra todos os episódios da série
//        System.out.println("\nLista de Episódios");
//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream()
//                        .map(d -> new Episodio(t.numero(), d)))
//                .collect(Collectors.toList());
//        episodios.forEach(System.out::println);


        //*** Mostra os anos em que a série foi produzida
//        List<Integer> anosSerie = episodios.stream()
//                .filter(e -> e.getDataLancamento()!= null)
//                .map(e -> e.getDataLancamento().getYear())
//                .distinct()
//                .collect(Collectors.toList());
//        int anoInicio = anosSerie.stream().min(Integer::compareTo).get();
//        int anoFim = anosSerie.stream().max(Integer::compareTo).get();
//        System.out.printf("\nSérie produzida entre os anos %d e %d.\n", anoInicio, anoFim);

        //*** Busca de episódios a partir de uma ano específico
//        System.out.println("\nA partir de que ano deseja ver os episódios: ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano,1,1);
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento()!= null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(t -> System.out.println(
//                        "Temporada: "+ t.getTemporada() +
//                                " - Episódio: " + t.getTitulo() +
//                                " - Data de Lançamento: " + t.getDataLancamento()
//                ));

        //*** Busca de um episódio a partir do nome do episódio
//        System.out.println("Digite um trecho do episódio procurado: ");
//        var trechoBuscado = leitura.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toLowerCase().contains(trechoBuscado.toLowerCase()))
//                .findFirst();
//        if(episodioBuscado.isPresent()){
//            System.out.println("Episódio Encontrado!");
//            System.out.println(episodioBuscado);
//        }else{
//            System.out.println("Episódio não encontrado!");
//        }

        //*** Obtendo estatísticas
//        Map<Integer, Double> avaliacaoPorTemporada = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getAvaliacao)));
//        for(int i=1 ; i<= avaliacaoPorTemporada.size(); i++){
//            System.out.printf("Temporada: %d - Avaliação: %.2f\n", i, avaliacaoPorTemporada.get(i));
//        }
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
//        System.out.println(est);
//        System.out.println("Média: "+est.getAverage());
//        System.out.println("Melhor episódio: "+est.getMax());
//        System.out.println("Pior episódio: "+est.getMin());
//        System.out.println("Qtde de episódios: "+est.getCount());

    }

    private void buscarSerieNaWeb() {
        DadosSerie dadosSerie = getDadosSerie();
        Serie serie = new Serie(dadosSerie);
        //listaSeriesBuscadas.add(dadosSerie);
        repositorio.save(serie);
        System.out.println("Série: " + dadosSerie.titulo());
        System.out.println("Salva com sucesso");
    }

    private DadosSerie getDadosSerie(){
        System.out.println("Digite o nome da Série: ");
        var nomeSerie = leitura.nextLine();
        var json = consumoAPI.obterDados(ENDERECO+nomeSerie.replace(" ",  "+")+APIKEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        return dadosSerie;
    }

    private void buscarEpisodiosPorSerie(){
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i=1; i<=dadosSerie.totalTemporadas(); i++){
            var json = consumoAPI.obterDados(ENDERECO+dadosSerie.titulo().replace(" ", "+")+"&Season="+i+APIKEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }

    private void listarSeriesBuscadas(){
        List<Serie> series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }



}
