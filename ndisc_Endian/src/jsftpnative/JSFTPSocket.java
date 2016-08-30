package jsftpnative;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.StringTokenizer;

public class JSFTPSocket extends JSFTPCommon
{
   protected boolean Send_ServiceCmd(int nConnID, String strServiceCmd, SocketChannel sc) throws Exception
   {
      int nServiceCodeLen = -1;
      boolean bRet = false;

      String strFormatedServiceCodeLen = null;

      ByteBuffer buffer = null;
      byte[] byteBuf = null;

      try
      {
         nServiceCodeLen = getLength(strServiceCmd);
         strFormatedServiceCodeLen = getFormatedServiceCodeLen(nServiceCodeLen);

         // step 1) send length
         byteBuf = strFormatedServiceCodeLen.getBytes();
         buffer = ByteBuffer.allocateDirect(byteBuf.length);
         buffer.put(byteBuf);
         buffer.flip();
         while (buffer.hasRemaining())
         {
            sc.write(buffer);
         }

         // step 2) send message
         byteBuf = strServiceCmd.getBytes();
         buffer = ByteBuffer.allocateDirect(byteBuf.length);
         buffer.put(byteBuf);
         buffer.flip();
         while (buffer.hasRemaining())
         {
            sc.write(buffer);
         }

         bRet = true;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         String strException = "send_ServiceCmd() : " + e.getMessage();
         JSFTPDebug.writeTrace(nConnID, strException);
         throw new Exception(strException);
      }
      finally
      {
         buffer.clear();
      }

      JSFTPDebug.writeTrace(nConnID, "send_ServiceCmd() Completed");

      return bRet;
   }

   protected String[] Recv_Response(int nConnID, String strServiceCode, SocketChannel sc) throws Exception
   {
      String[] arrRet = null;
      String strRet = null;

      ByteBuffer buffer = null;

      try
      {
         // step 1) read length - 10 length
         buffer = ByteBuffer.allocateDirect(SFTP_SINFO_FORMAT_LEN);
         while (buffer.position() < SFTP_SINFO_FORMAT_LEN)
         {
            sc.read(buffer);
         }
         buffer.flip();
         strRet = m_decoder.decode(buffer).toString();
         JSFTPDebug.writeTrace(nConnID, "Recv_Response() Message - [" + strRet + "]");

         // step 2) read message
         buffer = ByteBuffer.allocateDirect(Integer.parseInt(strRet));
         while (buffer.position() < Integer.parseInt(strRet))
         {
            sc.read(buffer);
         }
         buffer.flip();
         strRet = m_decoder.decode(buffer).toString();
         JSFTPDebug.writeTrace(nConnID, "Recv_Response() Message - [" + strRet + "]");

         if (SFTP_CMD_UPLOAD.equals(strServiceCode) || SFTP_CMD_MAKEDIR.equals(strServiceCode)
               || SFTP_CMD_EXISTFILE.equals(strServiceCode) || SFTP_CMD_DELETEFILE.equals(strServiceCode))
         {
            arrRet = new String[2];

            StringTokenizer sTK = new StringTokenizer(strRet, SFTP_DELIMSTR);

            arrRet[0] = sTK.nextToken(); // 리턴 코드
            arrRet[1] = sTK.nextToken(); // 리턴 메세지
         }
         else if (SFTP_CMD_DOWNLOAD.equals(strServiceCode) || SFTP_CMD_GETCONF.equals(strServiceCode))
         {
            StringTokenizer sTK = new StringTokenizer(strRet, SFTP_DELIMSTR);
            int nCount = sTK.countTokens();

            arrRet = new String[nCount];

            for (int i = 0; i < nCount; i++)
            {
               byte[] bByte = sTK.nextToken().getBytes();
               arrRet[i] = new String(bByte);
            }
         }

         JSFTPDebug.writeTrace(nConnID, "Response CODE = " + arrRet[0]);

      }
      catch (Exception e)
      {
         e.printStackTrace();
         String strException = "Recv_Response() : " + e.getMessage();
         JSFTPDebug.writeTrace(nConnID, strException);
         throw new Exception(strException);
      }
      finally
      {
         buffer.clear();
      }

      JSFTPDebug.writeTrace(nConnID, "Recv_Response() Completed");

      return arrRet;
   }

   private void sendNoCancel(SocketChannel sc) throws Exception
   {
      ByteBuffer buffer = null;
      byte[] byteBuf = null;

      try
      {
         byteBuf = SFTP_NOCANCEL_STR.getBytes();
         buffer = ByteBuffer.allocateDirect(byteBuf.length);
         buffer.put(byteBuf);
         buffer.flip();
         while (buffer.hasRemaining())
         {
            sc.write(buffer);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw e;
      }
      finally
      {
         buffer.clear();
      }
   }

   protected boolean uploadFile(int nConnID, int nNumOfFile, String[][] arrUploadFile, SocketChannel sc)
   {
      File file = null;
      ByteBuffer buffer = null;
      FileChannel inChannel = null;
      int nFileSize = 0;
      int nRemain = 0;
      int nAmount = 0;
      int nCount = 0;

      boolean bRet = false;

      try
      {
         for (int i = 0; i < nNumOfFile; i++)
         {
            file = new File(arrUploadFile[i][0]);
            inChannel = new FileInputStream(file).getChannel();

            nFileSize = Integer.parseInt(arrUploadFile[i][1]);
            ;
            nRemain = nFileSize;
            nAmount = SFTP_TRANSBUF_SIZE;
            nCount = 0;

            buffer = ByteBuffer.allocateDirect(SFTP_TRANSBUF_SIZE);
            while (nRemain > 0)
            {
               if (nRemain < SFTP_TRANSBUF_SIZE)
               {
                  nAmount = nRemain;
                  buffer = ByteBuffer.allocateDirect(nAmount);
               }

               // completely read
               while (buffer.position() < nAmount)
               {
                  inChannel.read(buffer);
               }
               buffer.flip();

               // completely write
               while (buffer.hasRemaining())
               {
                  sc.write(buffer);
               }
               buffer.clear();

               nRemain -= nAmount;
               nCount++;

               if ((0 == (nCount % SFTP_CHECK_COUNT)) || (0 >= nRemain))
               {
                  sendNoCancel(sc);
               }
            }

            file = null;
            inChannel.close();
         }

         bRet = true;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         String strException = "uploadFile() : " + e.getMessage();
         JSFTPDebug.writeTrace(nConnID, strException);
         bRet = false;
      }
      finally
      {
         try
         {
            file = null;
            inChannel.close();
         }
         catch (Exception ex)
         {
            ;
         }
      }

      JSFTPDebug.writeTrace(nConnID, "uploadFile() Completed");

      return bRet;
   }

   protected boolean downloadFile(int nConnID, int nNumOfFile, String[][] arrDownFile, SocketChannel sc)
   {
      File file = null;
      ByteBuffer buffer = null;
      FileChannel outChannel = null;
      int nFileSize = 0;
      int nRemain = 0;
      int nAmount = 0;
      int nCount = 0;

      boolean bRet = false;

      try
      {
         for (int i = 0; i < nNumOfFile; i++)
         {
            file = new File(arrDownFile[i][0]);
            outChannel = new FileOutputStream(file).getChannel();

            nFileSize = Integer.parseInt(arrDownFile[i][2]);
            nRemain = nFileSize;
            nAmount = SFTP_TRANSBUF_SIZE;
            nCount = 0;

            buffer = ByteBuffer.allocateDirect(SFTP_TRANSBUF_SIZE);
            while (nRemain > 0)
            {
               if (nRemain < SFTP_TRANSBUF_SIZE)
               {
                  nAmount = nRemain;
                  buffer = ByteBuffer.allocateDirect(nAmount);
               }

               // completely read
               while (buffer.position() < nAmount)
               {
                  sc.read(buffer);
               }
               buffer.flip();

               // completely write
               while (buffer.hasRemaining())
               {
                  outChannel.write(buffer);
               }
               buffer.clear();

               nRemain -= nAmount;
               nCount++;

               if ((0 == (nCount % SFTP_CHECK_COUNT)) || (0 >= nRemain))
               {
                  sendNoCancel(sc);
               }
            }

            file = null;
            outChannel.close();
         }

         bRet = true;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         String strException = "downloadFile() : " + e.getMessage();
         JSFTPDebug.writeTrace(nConnID, strException);
         bRet = false;
      }
      finally
      {
         try
         {
            file = null;
            outChannel.close();
         }
         catch (Exception ex)
         {
            ;
         }
      }

      JSFTPDebug.writeTrace(nConnID, "downloadFile() Completed");

      return bRet;
   }
}
