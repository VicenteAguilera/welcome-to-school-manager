package com.github.welcome_to_school_manager.helpers.utility;

import org.apache.commons.codec.binary.Base64;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encriptacion {
    public String encryptBase64(String data)
    {
        byte encodedData[] = Base64.encodeBase64(data.getBytes());
        return new String(encodedData);
    }
    public String decryptBase64(String data)
    {
        byte encodedData[] = Base64.decodeBase64(data.getBytes());
        return new String(encodedData);
    }

    /**
     * AES se encripta en multiplos de 16 la llave
     */
    private static String key ="123456D$L)neUvyp";
    public static byte keyAES[] = key.getBytes(StandardCharsets.UTF_8);
    public final String ALGORITHM="AES";

    public String encryptAE(String data)
    {
        byte dataToSent[] = data.getBytes();
        byte encryptedData[] = null;
        byte encryptedByteValue[]=null;

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyAES,ALGORITHM);
        Cipher cipher =null;
        try
        {
            cipher = Cipher.getInstance(ALGORITHM);//se inicia diciendo que algoritmo es conn el que encripto
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);//inicia la encripacion y el envio la llave generada
            // se manda esa llave y no la mia por que em añunas operaciones hay un límite específico para hacer la operacion,
            // entonces se untiliza secretKeySpec que es autogeneada a partir de mi key y necesita esa llave para que init , ademas
            // esa llave es la que cierra y desencripta.

            //no se usa AES sola, ya que en lacgunas ocaciones podemppos perdes informacion
            //para ello primero nos refujiamos en base64 y con ello no perdemos info a la hora de decodificar
            encryptedData = cipher.doFinal(dataToSent);// almacenamos los datos encriptados en AES
            // encriptamos en base 64 lo que ya está en AES
            encryptedByteValue = new Base64().encode(encryptedData);
            return new String(encryptedByteValue);
        }
        catch (Exception ex)
        {
            //ocurrio un error
            return null;
        }
    }
    public String decryptAE(String data)
    {
        //como la encriptacion queda en base 64 comenzamos desencriptando en base 64
        byte decryptedData[] = new Base64().decode(data);

        byte dataToSent[] = null;


        SecretKeySpec secretKeySpec = new SecretKeySpec(keyAES,ALGORITHM);
        Cipher cipher =null;
        try
        {
            cipher = Cipher.getInstance(ALGORITHM);//se inicia diciendo que algoritmo es conn el que se desencripta
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);//inicia la encripacion y el envio la llave generada
            // se manda esa llave y no la mia por que em añunas operaciones hay un límite específico para hacer la operacion,
            // entonces se untiliza secretKeySpec que es autogeneada a partir de mi key y necesita esa llave para que init , ademas
            // esa llave es la que cierra y desencripta.

            //no se usa AES sola, ya que en lacgunas ocaciones podemppos perdes informacion
            //para ello primero nos refujiamos en base64 y con ello no perdemos info a la hora de decodificar
            dataToSent = cipher.doFinal(decryptedData);// almacenamos los datos desencriptados en AES

            return new String(dataToSent);
        }
        catch (Exception ex)
        {
            //ocurrio un error
            return null;
        }
    }
}
