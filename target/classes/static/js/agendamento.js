/**
 * busca as especialidades com auto-complete
 */
$("#especialidade").autocomplete({
    source: function (request, response) {
        $.ajax({
            method: "GET",
            url: "/especialidades/titulo",
            data: {
                termo: request.term
            },
            success: function (data) {
                response(data);
            }
        });
    }
});

/**
 * após a especialidade ser selecionado busca
 * os médicos referentes e os adiciona na página com
 * radio
 */
$('#especialidade').on('blur', function() {
    $('div').remove(".custom-radio");
    var titulo = $(this).val();
    if ( titulo != '' ) {
        $.get( "/medicos/especialidade/titulo/" + titulo , function( result ) {

            var ultimo = result.length - 1;

            $.each(result, function (k, v) {

                if ( k == ultimo ) {
                    $("#medicos").append(
                        '<div class="custom-control custom-radio">'
                        +  '<input class="custom-control-input" type="radio" id="customRadio'+ k +'" name="medico.id" value="'+ v.id +'" required>'
                        +  '<label class="custom-control-label" for="customRadio'+ k +'">'+ v.nome +'</label>'
                        +  '<div class="invalid-feedback">Médico é obrigatório</div>'
                        +'</div>'
                    );
                } else {
                    $("#medicos").append(
                        '<div class="custom-control custom-radio">'
                        +  '<input class="custom-control-input" type="radio" id="customRadio'+ k +'" name="medico.id" value="'+ v.id +'" required>'
                        +  '<label class="custom-control-label" for="customRadio'+ k +'">'+ v.nome +'</label>'
                        +'</div>'
                    );
                }
            });
        });
    }
});
