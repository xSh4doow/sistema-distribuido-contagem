public class Pedido extends Comunicado
{
    private byte[] numeros;
    private byte procurado;

    public Pedido(byte[] numeros, byte procurado)
    {
        this.numeros = numeros;
        this.procurado = procurado;
    }

    public int contar()
    {
        int contagem = 0;
        for (byte numero : this.numeros)
        {
            if (numero == this.procurado)
                contagem++;
        }
        return contagem;
    }

    public byte[] getNumeros()
    {
        return this.numeros;
    }

    public byte getProcurado()
    {
        return this.procurado;
    }
}
