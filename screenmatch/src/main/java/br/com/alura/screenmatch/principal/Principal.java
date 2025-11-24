package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=f1540ab8";
    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();

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

    }


    private void buscarSerieNaWeb() {
        Serie serie = new Serie(getDadosSerie());
        if (series.stream().noneMatch(s -> s.getTitulo().equalsIgnoreCase(serie.getTitulo()))){
            salvarNoPostgres(serie);
            System.out.println("Série salva no BD.");
        }
        System.out.println(serie);
    }

    private DadosSerie getDadosSerie(){
        System.out.println("Digite o nome da Série: ");
        var nomeSerie = leitura.nextLine();
        var json = consumoAPI.obterDados(ENDERECO+nomeSerie.replace(" ",  "+")+APIKEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodiosPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Digite o nome da Série: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoAPI.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&Season=" + i + APIKEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new Episodio(t.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            salvarNoPostgres(serieEncontrada);
        }else{
            System.out.println("Série não encontrada.");
        }
    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void salvarNoPostgres (Serie serie){
        repositorio.save(serie);
        System.out.println("Série salva no BD.");
    }



}
