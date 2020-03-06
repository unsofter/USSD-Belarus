package by.unsofter.ussdbelarus;

public class USSD {
    public long   id;
    public String USSDCode;
    public String Info;
    public int    Type;
    public String Shablon;
    public int    Operator;

    public USSD (long id, String USSDCode, String Info, int Type, String Shablon, int Operator)
    {
        this.id       = id;
        this.USSDCode = USSDCode;
        this.Info     = Info;
        this.Type     = Type;
        this.Shablon  = Shablon;
        this.Operator = Operator;
    }
}
