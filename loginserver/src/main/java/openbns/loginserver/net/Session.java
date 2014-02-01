package openbns.loginserver.net;

import openbns.loginserver.crypt.KeyManager;
import openbns.loginserver.model.Account;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene Chipachenko
 * Date: 30.01.14
 * Time: 16:29
 */
public class Session
{
  private static final Random rnd = new Random();
  private BigInteger privateKey;
  private BigInteger exchangeKey;
  private BigInteger serverExchangeKey;
  private BigInteger sessionKey;
  private int sessionId;

  private Account account;

  public Session()
  {
    try
    {
      privateKey = KeyManager.getInstance().generatePrivateKey();
      exchangeKey = KeyManager.getInstance().generateExchangeKey( privateKey );
      sessionKey = new BigInteger( 64, rnd );
    }
    catch( NoSuchAlgorithmException e )
    {
      e.printStackTrace();
    }
  }

  public BigInteger generateServerExchangeKey() throws NoSuchAlgorithmException, IOException
  {
    byte[] passwordHash = account.getPassword();

    BigInteger hashedKey = KeyManager.getInstance().generateSharedKey( sessionKey.toByteArray(), passwordHash );
    BigInteger decKey = new BigInteger( "2" ).modPow( hashedKey, KeyManager.N );
    decKey = decKey.multiply( KeyManager.P ).mod( KeyManager.N );
    serverExchangeKey = exchangeKey.add( decKey ).mod( KeyManager.N );
    return serverExchangeKey;
  }

  public void setAccount( Account account )
  {
    this.account = account;
  }

  public BigInteger getPrivateKey()
  {
    return privateKey;
  }

  public BigInteger getExchangeKey()
  {
    return exchangeKey;
  }

  public BigInteger getServerExchangeKey()
  {
    return serverExchangeKey;
  }

  public BigInteger getSessionKey()
  {
    return sessionKey;
  }

  public int getSessionId()
  {
    return sessionId;
  }

  public Account getAccount()
  {
    return account;
  }

  @Override
  public String toString()
  {
    return "Session{" +
            "privateKey=" + privateKey +
            ", exchangeKey=" + exchangeKey +
            ", sessionKey=" + sessionKey +
            ", sessionId=" + sessionId +
            '}';
  }
}
