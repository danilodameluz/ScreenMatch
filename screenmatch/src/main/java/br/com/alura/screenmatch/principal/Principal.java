package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
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
    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibirMenu(){
        int opcao = -1;
        while (opcao != 0) {
            var menu = """
                    *** OPÇÕES ***
                    1 - BUSCAR SÉRIE NA WEB
                    2 - CARREGAR EPISÓDIOS
                    3 - LISTAR SÉRIES BUSCADAS
                    4 - BUSCAR SÉRIE POR TÍTULO
                    5 - BUSCAR SÉRIE POR ATOR
                    6 - BUSCAR SÉRIE POR CATEGORIA
                    7 - TOP 5 SÉRIES PESQUISADAS
                    8 - BUSCAR POR TEMPORADA E AVALIAÇÃO
                    9 - BUSCAR EPISÓDIO PELO NOME
                    10 - TOP 5 EPISÓDIOS POR SÉRIE
                    11 - EPISÓDIOS POR SÉRIE E ANO            
                                    
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
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarSeriePorCategoria();
                    break;
                case 7:
                    listarTop5Series();
                    break;
                case 8:
                    buscarPorTemporadasEAvaliacao();
                    break;
                case 9:
                    buscarEpisodiosPeloNome();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosPorSerieEAno();
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

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

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

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da Série: ");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()){
            System.out.println("Dados da Série: " + serieBusca.get());
        }else {
            System.out.println("Série não encontrada.");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do Ator: ");
        var nomeAtor = leitura.nextLine();

        System.out.println("Avaliações a partir de que valor: ");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesBuscadas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
            System.out.println("\nSéries em que " + nomeAtor + " trabalhou.");
            seriesBuscadas.forEach(s ->
                    System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
    }

    private void listarTop5Series() {
        List<Serie> seriesTop5 = repositorio.findTop5ByOrderByAvaliacaoDesc();
        System.out.println("\nTOP 5 - Séries");
        seriesTop5.forEach(s ->
                System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Digite a Categoria desejada: ");
        var nomeCategoria = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);
        List<Serie> seriesporCategoria = repositorio.findByGenero(categoria);
        seriesporCategoria.forEach(System.out::println);
    }

    private void buscarPorTemporadasEAvaliacao(){
        System.out.println("Qual o número máximo de temporadas: ");
        var numeroTemporadas = leitura.nextInt();
        System.out.println("Qual a avaliação desejada: ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio
                .seriesPorTemporadaEAvaliacao(numeroTemporadas,avaliacao);
        seriesEncontradas.forEach(System.out::println);
    }

    private void buscarEpisodiosPeloNome(){
        System.out.println("Digite o trecho do Episódio: ");
        var nomeEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorNome(nomeEpisodio);
        episodiosEncontrados.forEach(System.out::println);
    }

    private void topEpisodiosPorSerie(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e->
                    System.out.printf("Série: %s - Temporada: %s - Episódio: %s - %s - Avaliação: %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(),
                            e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosPorSerieEAno(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite: ");
            var anoLancamento = leitura.nextInt();
            List<Episodio> episodiosPorSerieEAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosPorSerieEAno.forEach(e->
                    System.out.printf("Série: %s - Temporada: %s - Episódio: %s - %s - Avaliação: %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(),
                            e.getTitulo(), e.getAvaliacao()));
        }
    }

}
