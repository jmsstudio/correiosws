package br.com.jmsstudio.correios.client;

import br.com.jmsstudio.correios.cep.AtendeCliente;
import br.com.jmsstudio.correios.cep.AtendeClienteService;
import br.com.jmsstudio.correios.cep.EnderecoERP;
import br.com.jmsstudio.correios.soap.CResultado;
import br.com.jmsstudio.correios.soap.CServico;
import br.com.jmsstudio.correios.soap.CalcPrecoPrazoWS;
import br.com.jmsstudio.correios.soap.CalcPrecoPrazoWSSoap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Realiza pesquisas em 2 webservices públicos dos correios:
 * - http://ws.correios.com.br/calculador/CalcPrecoPrazo.asmx?WSDL
 * - https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente?wsdl
 */
public class TestCorreiosClient {

    public static void main(String[] args) throws Exception {
        System.out.println("Cep origem: ");
        Scanner scanner = new Scanner(System.in);

        String cepOrigem = scanner.next();
        consultarCEP(cepOrigem);


        System.out.println("Cep destino: ");
        String cepDestino = scanner.next();
        consultarCEP(cepDestino);

        System.out.println("Frete: ");
        consultarPrecoFrete(cepOrigem, cepDestino);
    }

    public static void consultarPrecoFrete(String cepOrigem, String cepDestino) {
        CalcPrecoPrazoWSSoap calcPrecoPrazoWS = new CalcPrecoPrazoWS().getCalcPrecoPrazoWSSoap();

        String codigoSedex = "40010";
        String peso3kg = "3";
        BigDecimal comprimento20cm = new BigDecimal(20);
        BigDecimal altura10cm = new BigDecimal(10);
        BigDecimal largura15cm = new BigDecimal(15);
        BigDecimal diametro10cm = new BigDecimal(10);
        int formatoEncomendaCaixa = 1; // 1 é caixa ou pacote
        BigDecimal semValorDeclarado= BigDecimal.ZERO;
        String semEntregueEmMaos = "N";
        String semAvisoRecebimento = "N";
        String semCodigoEmpresa = "";
        String semSenhaEmpresa = "";

        CResultado resultado = calcPrecoPrazoWS.calcPrecoPrazo(semCodigoEmpresa, semSenhaEmpresa, codigoSedex,
                cepOrigem, cepDestino, peso3kg, formatoEncomendaCaixa, comprimento20cm, altura10cm, largura15cm,
                diametro10cm, semEntregueEmMaos, semValorDeclarado, semAvisoRecebimento);

        List<CServico> servicos = resultado.getServicos().getCServico();
        String valorFrete = servicos.get(0).getValor();
        System.out.printf("Frete de %s para %s é de %s R$ %n", cepOrigem, cepDestino, valorFrete);
    }

    public static void consultarCEP(String cep) throws Exception {
        AtendeCliente atendeClientePort = new AtendeClienteService().getAtendeClientePort();
        EnderecoERP endereco = atendeClientePort.consultaCEP(cep);

        System.out.println("Dados do CEP " + cep);
        System.out.println("Cidade: " + endereco.getCidade() + "/" + endereco.getUf());
        System.out.println("Endereço: " + endereco.getEnd());
        System.out.println("Bairro: " + endereco.getBairro());

    }

}
