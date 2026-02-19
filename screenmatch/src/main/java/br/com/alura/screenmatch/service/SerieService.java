package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repositorio;

    public List<SerieDTO> obterTodasAsSeries(){
        return converteListSeriesEmListSeriesDTO(repositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteListSeriesEmListSeriesDTO(repositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos(){
        return converteListSeriesEmListSeriesDTO(repositorio.encontrarEpisodiosMaisRecentes());
    }

    public SerieDTO obterSeriePorId(Long id) {
        Optional<Serie> serie = repositorio.findById(id);

        if(serie.isPresent()){
            Serie s = serie.get();
            return converteSerieEmSerieDTO(s);
        }
        return null;
    }

    public List<EpisodioDTO> obterTodasTemporadas(Long id) {
        Optional<Serie> serie = repositorio.findById(id);

        if(serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(this::converteEpisodioEmEpisodioDTO)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private List<SerieDTO> converteListSeriesEmListSeriesDTO(List<Serie> series){
        return series.stream()
                .map(this::converteSerieEmSerieDTO)
                .collect(Collectors.toList());
    }

    private SerieDTO converteSerieEmSerieDTO(Serie serie){
        return new SerieDTO(
                serie.getId(),
                serie.getTitulo(),
                serie.getTotalTemporadas(),
                serie.getAvaliacao(),
                serie.getGenero(),
                serie.getAtores(),
                serie.getPoster(),
                serie.getSinopse()
        );
    }

    private EpisodioDTO converteEpisodioEmEpisodioDTO(Episodio episodio){
        return new EpisodioDTO(
                episodio.getTemporada(),
                episodio.getNumeroEpisodio(),
                episodio.getTitulo()
        );
    }

}
