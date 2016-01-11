package isens.hba1c_analyzer;

import isens.hba1c_analyzer.Model.AboutModel;
import isens.hba1c_analyzer.Model.Hardware;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.Model.MainTimer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

public class SerialPort {
	
	private Barcode mBarcode;
	private ResultActivity mResultActivity;
	private LanguageModel mLanguageModel;
	
	/* Board Serial set-up */
	private static FileDescriptor BoardFD;
	private static FileInputStream BoardFIS;
	private FileOutputStream boardFOS;
	
	private BoardTxThread mBoardTxThread;
	private static BoardRxThread mBoardRxThread;
	
	/* Printer Serial set-up */
	private static FileDescriptor PrinterFD;
	private static FileInputStream PrinterFIS;
	private FileOutputStream printerFOS;
	
	private PrinterTxThread pPrinterTxThread;
	private RxUART2fromPC mRxUART2fromPC;
	
	/* Barcode Serial set-up */
	public static FileDescriptor BarcodeFD;
	public static FileInputStream BarcodeFIS;
	private FileOutputStream barcodFOS;
	public static BarcodeRxThread bBarcodeRxThread;
	
	public FileDescriptor mFd;
	public static FileInputStream HHBarcodeFIS;
	public static HHBarcodeRxThread hBarcodeRxThread;
	
	/* CRC code Table */
	private char CRC_16_SEED = 0x0000;
	private char[] CRC16_TABLE = {
        0x0000,0x1021,0x2042,0x3063,0x4084,0x50a5,0x60c6,0x70e7,
        0x8108,0x9129,0xa14a,0xb16b,0xc18c,0xd1ad,0xe1ce,0xf1ef,
        0x1231,0x0210,0x3273,0x2252,0x52b5,0x4294,0x72f7,0x62d6,
        0x9339,0x8318,0xb37b,0xa35a,0xd3bd,0xc39c,0xf3ff,0xe3de,
        0x2462,0x3443,0x0420,0x1401,0x64e6,0x74c7,0x44a4,0x5485,
        0xa56a,0xb54b,0x8528,0x9509,0xe5ee,0xf5cf,0xc5ac,0xd58d,
        0x3653,0x2672,0x1611,0x0630,0x76d7,0x66f6,0x5695,0x46b4,
        0xb75b,0xa77a,0x9719,0x8738,0xf7df,0xe7fe,0xd79d,0xc7bc,
        0x48c4,0x58e5,0x6886,0x78a7,0x0840,0x1861,0x2802,0x3823,
        0xc9cc,0xd9ed,0xe98e,0xf9af,0x8948,0x9969,0xa90a,0xb92b,
        0x5af5,0x4ad4,0x7ab7,0x6a96,0x1a71,0x0a50,0x3a33,0x2a12,
        0xdbfd,0xcbdc,0xfbbf,0xeb9e,0x9b79,0x8b58,0xbb3b,0xab1a,
        0x6ca6,0x7c87,0x4ce4,0x5cc5,0x2c22,0x3c03,0x0c60,0x1c41,
        0xedae,0xfd8f,0xcdec,0xddcd,0xad2a,0xbd0b,0x8d68,0x9d49,
        0x7e97,0x6eb6,0x5ed5,0x4ef4,0x3e13,0x2e32,0x1e51,0x0e70,
        0xff9f,0xefbe,0xdfdd,0xcffc,0xbf1b,0xaf3a,0x9f59,0x8f78,
        0x9188,0x81a9,0xb1ca,0xa1eb,0xd10c,0xc12d,0xf14e,0xe16f,
        0x1080,0x00a1,0x30c2,0x20e3,0x5004,0x4025,0x7046,0x6067,
        0x83b9,0x9398,0xa3fb,0xb3da,0xc33d,0xd31c,0xe37f,0xf35e,
        0x02b1,0x1290,0x22f3,0x32d2,0x4235,0x5214,0x6277,0x7256,
        0xb5ea,0xa5cb,0x95a8,0x8589,0xf56e,0xe54f,0xd52c,0xc50d,
        0x34e2,0x24c3,0x14a0,0x0481,0x7466,0x6447,0x5424,0x4405,
        0xa7db,0xb7fa,0x8799,0x97b8,0xe75f,0xf77e,0xc71d,0xd73c,
        0x26d3,0x36f2,0x0691,0x16b0,0x6657,0x7676,0x4615,0x5634,
        0xd94c,0xc96d,0xf90e,0xe92f,0x99c8,0x89e9,0xb98a,0xa9ab,
        0x5844,0x4865,0x7806,0x6827,0x18c0,0x08e1,0x3882,0x28a3,
        0xcb7d,0xdb5c,0xeb3f,0xfb1e,0x8bf9,0x9bd8,0xabbb,0xbb9a,
        0x4a75,0x5a54,0x6a37,0x7a16,0x0af1,0x1ad0,0x2ab3,0x3a92,
        0xfd2e,0xed0f,0xdd6c,0xcd4d,0xbdaa,0xad8b,0x9de8,0x8dc9,
        0x7c26,0x6c07,0x5c64,0x4c45,0x3ca2,0x2c83,0x1ce0,0x0cc1,
        0xef1f,0xff3e,0xcf5d,0xdf7c,0xaf9b,0xbfba,0x8fd9,0x9ff8,
        0x6e17,0x7e36,0x4e55,0x5e74,0x2e93,0x3eb2,0x0ed1,0x1ef0
    };
	
	public enum CtrTarget {MotorSet, NormalSet}
	
	final static byte STX  = 0x7E,
					  ETX  = 0x7F,
					  STX2 = 0x02,
					  ETX2 = 0x03,
					  LF   = 0x0A,
					  CR   = 0x0D,
					  ESC  = 0x1B,
					  GS   = 0x1D;
	
	public static final byte PRINT_RESULT  = 1,
							 PRINT_RECORD  = 2,
							 PC_QC_CAL     = 3;
	
	final static int UART_RX_MASK = 128;
	final static byte AMB_MSG_RX_MASK = 4,
					  BOARD_INPUT_MASK = 8;
	public static final byte BOARD_INPUT_BUFFER = 8;
	
	private static byte BoardInputBuffer[] = new byte[BOARD_INPUT_BUFFER],
						BoardRxBuffer[][] = new byte[BOARD_INPUT_MASK][BOARD_INPUT_BUFFER];
	
	private static String BoardRxData = "",
						  BoardMsgBuffer[] = new String[UART_RX_MASK],
						  SensorMsgBuffer[] = new String[UART_RX_MASK];

	private static int BoardInputHead = 0,
					   BoardInputTail = 0,
					   BoardRxHead = 0,
					   BoardRxTail = 0,
					   BoardMsgHead = 0,
					   BoardMsgTail = 0,
					   SensorMsgHead = 0,
					   SensorMsgTail = 0;
					   
	private static boolean BoardRxFlag = false;
	
	private byte uART2InputBuffer[] = new byte[BOARD_INPUT_BUFFER],
				 uART2RxBuffer[][] = new byte[BOARD_INPUT_BUFFER][BOARD_INPUT_BUFFER];

	private byte pCMsgBuffer[][] = new byte[UART_RX_MASK][BOARD_INPUT_BUFFER];
	private ArrayList<Byte> uART2RxDataList;
	 
	private int uART2InputHead = 0,
				uART2InputTail = 0,
				uART2RxHead = 0,
				uART2RxTail = 0,
				uART2RxIdx = 0,
		   		pCMsgHead = 0,
		   		pCMsgTail = 0;	

	private boolean uART2RxFlag = false;
	
	final static byte BARCODE_RX_BUFFER_SIZE = 32,
					  BARCODE_BUFFER_CNT_SIZE = 16,
					  BARCODE_BUFFER_INDEX_SIZE = 32,
					  A1C_MAX_BUFFER_INDEX = 18,
					  A1C_QC_MAX_BUFFER_INDEX = 11,
					  A1C_MIN_BUFFER_INDEX = 2;
	
	private static byte BarcodeRxBuffer[] = new byte[BARCODE_RX_BUFFER_SIZE],
						BarcodeAppendBuffer[][] = new byte[BARCODE_BUFFER_CNT_SIZE][BARCODE_BUFFER_INDEX_SIZE],
						BarcodeBufCnt = 0,
						BarcodeAppendCnt = 0;

	private static byte HHBarcodeRxBuffer[] = new byte[BARCODE_RX_BUFFER_SIZE],
						HHBarcodeAppendBuffer[][] = new byte[BARCODE_BUFFER_CNT_SIZE][BARCODE_BUFFER_INDEX_SIZE],
						HHBarcodeBufCnt = 0,
						HHBarcodeAppendCnt = 0;
	
	public static byte BarcodeBufIndex = 0;
	public static byte HHBarcodeBufIndex = 0;
	
	public static boolean BarcodeReadStart = false,
						  HHBarcodeReadStart = false;
	
	private class BoardTxThread extends Thread { // Instruction for a board

		private String message;
		private CtrTarget target;
		
		BoardTxThread(String message, CtrTarget target) {
			
			this.message = message;
			this.target = target;
		}
		
		public synchronized void run() {
			
			try {
				
				boardFOS = new FileOutputStream(BoardFD);				

				if (boardFOS != null) {
					
					boardFOS.write(STX2);
//					Log.w("BoardTxThread", "" + message);
					switch(target) {
										
					case MotorSet	:
						boardFOS.write(new String("A").getBytes());
						boardFOS.write(new String("012").getBytes()); // Motor shaking angle, default : 012
						boardFOS.write(new String("R").getBytes());
						boardFOS.write(message.getBytes()); // Motor shaking time, default : 6.5 * 10(sec) = 0065
						break;
						
					default :
						boardFOS.write(message.getBytes());
						break;
					}
					
					boardFOS.write(ETX2);
					
				} else {
					
					return;
				}
				
			} catch (IOException e) {
				
				e.printStackTrace();					
				return;
			}
		}
	}
	
	public class PrinterTxThread extends Thread { // Instruction for a printer
		
		private Context context;
		private Activity activity;
		
		String type = "",
			   primary = "",
			   unit = "",
			   range;
		
		String charSet = "";
		
		StringBuffer txData;
		
		byte mode;
		
		int	pIdx,
			pLen,
			oIdx,
			oLen;
		
		PrinterTxThread(Activity activity, Context context, byte mode, StringBuffer txData) {
			
			this.activity = activity;
			this.context = context;
			this.mode = mode;
			this.txData = txData;
		}
		
		public void run() {
			
			mLanguageModel = new LanguageModel(activity);
			charSet = getCharSet(mLanguageModel.getSettingLanguage());
			
			try {
				
				printerFOS = new FileOutputStream(PrinterFD);		
				
				if (printerFOS != null) {
					
					pIdx = 24 + 2;
					pLen = Integer.parseInt(txData.substring(24, pIdx));
					oIdx = pIdx + pLen + 2;
					oLen = Integer.parseInt(txData.substring(pIdx + pLen, oIdx));
					
					printerFOS.write(STX2);
					
					/* i-SENS */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write("i-SENS, Inc.".getBytes());
					
					/* A1Care */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(GS); 
					printerFOS.write(0x21); // size of character 
					printerFOS.write(0x01); // 1 times of width and 2 times of height
					printerFOS.write("A1Care".getBytes());
					
					/* Start Line */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(GS); 
					printerFOS.write(0x21); // size of character 
					printerFOS.write(0x00); // 1 times of width and 1 times of height
					printerFOS.write("------------------------------------".getBytes());
					
					if(mode == PRINT_RESULT) {
					
						printerFOS.write(LF);
						printerFOS.write(CR);
						printerFOS.write(context.getResources().getString(R.string.resultdata).getBytes(charSet));
						
					} else if(mode == PRINT_RECORD) {
					
						printerFOS.write(LF);
						printerFOS.write(CR);
						printerFOS.write(context.getResources().getString(R.string.recorddata).getBytes(charSet));
					}
					
					/* Test Date */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(context.getResources().getString(R.string.testdate).getBytes(charSet));
					
					setGap();
					
					/* Year */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(0, 4).getBytes());
					printerFOS.write(".".getBytes());
					
					/* Month */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(4, 6).getBytes());
					printerFOS.write(".".getBytes());
					
					/* Day */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(6, 8).getBytes());
					printerFOS.write(" ".getBytes());
					
					/* Hour */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(10, 12).getBytes());
					printerFOS.write(":".getBytes());
					
					/* Minute */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(12, 14).getBytes());
					printerFOS.write(" ".getBytes());
					
					/* AM/PM */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(8, 10).getBytes());
					
					/* Type */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(context.getResources().getString(R.string.type).getBytes(charSet));
					
					setGap();
					
					/* HbA1c */
					type = txData.substring(18, 19);
					
					if(type.equals("W") || type.equals("X")) type = "Control HbA1c";
					else if(type.equals("Y") || type.equals("Z")) type = "Control ACR";
					else if(type.equals("E")) type = "ACR";
					else  type = "HbA1c";					
					
					printerFOS.write(CR);
					printerFOS.write(type.getBytes());
					
					/* Primary */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(context.getResources().getString(R.string.primary).getBytes(charSet));
					
					setGap();
					
					primary = txData.substring(oIdx + oLen, oIdx + oLen + 1);
					
					if(primary.equals("0")) {
						
						primary = "NGSP";
						unit = " %";
						range = "4.0 - 6.0";

					} else {
						
						primary = "IFCC";
						unit = " mmol/mol";
						range = "20 - 42";
					}
					
					/* HbA1c percentage */
					printerFOS.write(CR);
					printerFOS.write(primary.getBytes());
					
					/* Result */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(context.getResources().getString(R.string.result).getBytes(charSet));
					
					setGap();
					
					/* HbA1c */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(oIdx + oLen + 1).getBytes());
					printerFOS.write(unit.getBytes());
					
					/* Reference Range */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(context.getResources().getString(R.string.refrange).getBytes(charSet));
					
					setGap();
					
					/* 4.0 - 6.0% */
					printerFOS.write(CR);
					printerFOS.write((range + unit).getBytes());
					
					/* Test No. */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(context.getResources().getString(R.string.testnum).getBytes(charSet));
					
					setGap();
					
					/* Test number */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(14, 18).getBytes());

					/* Cartridge Lot */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(context.getResources().getString(R.string.lot).getBytes(charSet));
					
					setGap();
					
					/* Lot number */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(19, 24).getBytes());
					
					/* PID */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(context.getResources().getString(R.string.patientid).getBytes(charSet));
					
					setGap();
					
					/* Patient ID */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(pIdx, pIdx + pLen).getBytes());
										
					/* OID */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write(context.getResources().getString(R.string.operatorid).getBytes(charSet));
					
					setGap();
					
					/* Operator ID */
					printerFOS.write(CR);
					printerFOS.write(txData.substring(oIdx, oIdx + oLen).getBytes());
					
					/* End Line */
					printerFOS.write(LF);
					printerFOS.write(CR);
					printerFOS.write("------------------------------------".getBytes());
					
					printerFOS.write(ESC);
					printerFOS.write(0x64); // print and feed n lines
					printerFOS.write(0x0A); // lines of number
					
					printerFOS.write(GS); // cut paper
					printerFOS.write(0x56);
					printerFOS.write(0x30);
					
					printerFOS.write(ETX2);
				}					
						
			} catch (IOException e) {
				
				e.printStackTrace();					
				return;
			}
		}
		
		private void setGap() {
			
			try {
			
				printerFOS.write(ESC);
				printerFOS.write(0x24);
				printerFOS.write(0xC8);
				printerFOS.write(0x00);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public StringBuffer getPrintData(ArrayList<String> txtList) {
		
		DecimalFormat lenDfm = new DecimalFormat("00");
		StringBuffer txData = new StringBuffer();
		String dateTime = txtList.get(1);
		
		txData.append(dateTime.substring(0, 4));
		txData.append(dateTime.substring(4, 6));
		txData.append(dateTime.substring(6, 8));
		txData.append(dateTime.substring(8, 10));
		txData.append(dateTime.substring(10, 12));
		txData.append(dateTime.substring(12, 14));
		txData.append(txtList.get(0));
		
		txData.append(txtList.get(2));
		txData.append(txtList.get(5));
		txData.append(lenDfm.format(txtList.get(6).length()));
		txData.append(txtList.get(6));
		txData.append(lenDfm.format(txtList.get(7).length()));
		txData.append(txtList.get(7));
		txData.append(txtList.get(4));
		txData.append(txtList.get(3));
		
		return txData;
	}
	
	private String getCharSet(int idx) {
		
		String encoding = "";
		
		switch(idx) {
		
		case LanguageModel.KO	:
			encoding = "EUC-KR";
			break;
			
		case LanguageModel.EN	:
			encoding = "US-ASCII";
			break;
			
		case LanguageModel.ZH:
			encoding = "GB2312";
			break;
			
		case LanguageModel.JA	:
			encoding = "Shift_JIS";
			break;
			
		default	:
			encoding = "US-ASCII";
			break;
		}	
		
		return encoding;
	}

	private class TxUART2toPC extends Thread {
		
		private int mode;
		private byte[] txData;
		
		TxUART2toPC(int mode, byte[] txData) {
		
			this.mode = mode;
			this.txData = new byte[txData.length];
			this.txData = txData;
		}
		
		public synchronized void run() {
			
			try {
				
				printerFOS = new FileOutputStream(PrinterFD);

				if (printerFOS != null) {
					
					switch(mode) {
					
					case PC_QC_CAL	 :
						printerFOS.write(txData);
						break;
						
					default	:
						break;
					}
					
				} else return;
				
			} catch (IOException e) {
				
				e.printStackTrace();					
				return;
			}
		}
	}

	public class BoardRxThread extends Thread { // Receiving from a board
		
		public void run() {
			
			while(!isInterrupted()) {
				
				int size;
				
				try {

					if(BoardFIS != null) {

						BoardInputBuffer = new byte[BOARD_INPUT_BUFFER];
						size = BoardFIS.read(BoardInputBuffer);
						
						BoardDataReceive(size);
					}
					
				} catch (IOException e) {
					
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	public synchronized void BoardDataReceive(int size) {
		
		int tmpHead;
		
		tmpHead = (BoardInputHead + 1) % BOARD_INPUT_MASK;
		
		if(tmpHead != BoardInputTail) {
			
			for(int i = 0; i < size; i++) {
				
				BoardRxBuffer[tmpHead][i] = BoardInputBuffer[i];
			}
			
			if(size != 8) BoardRxBuffer[tmpHead][size] = 0;
			BoardInputHead = tmpHead;
		}
	}
	
	public byte[] BoardInputData() {
		
		int tmpTail;
		
		if(BoardInputTail != BoardInputHead) {
			
			tmpTail = (BoardInputTail + 1) % BOARD_INPUT_MASK;
			BoardInputTail = tmpTail;
				
			return BoardRxBuffer[tmpTail];	
		
		} else return null;
	}
	
	public void BoardRxData() {
		
		BoardRxData mBoardRxData = new BoardRxData();
		mBoardRxData.start();
	}
	
	public class BoardRxData extends Thread {
		
		public void run() {
			
			byte[] tmpBuffer;
			byte tmpData;
			
			tmpBuffer = BoardInputData();
			
			if(tmpBuffer != null) {
				
				for(int i = 0; i < BOARD_INPUT_BUFFER; i++) {
					
					tmpData = tmpBuffer[i];

					if(tmpData == 0) break;
					
					if((tmpData != STX2) && BoardRxFlag) {
						
						if(tmpData == ETX2) {
							
							BoardMessageForm(BoardRxData);
							BoardRxFlag = false;
							
						} else BoardRxData += Character.toString((char) tmpData);	
						
					} else if(tmpData == STX2) {
						
						BoardRxFlag = true;
						BoardRxData = "";
					}
				}
			}
		}
	}
	
	public void BoardMessageForm(String tempStrData) {
		
		String tempStr;

		tempStr = tempStrData.substring(0, 1);
//		Log.w("BoardMessageForm", "" + tempStrData);
		if(!tempStr.equals("S") && !tempStr.equals("E")) {
			
			BoardMessageBuffer(tempStrData);
			
		} else if(tempStr.equals("S")) {
			
			SensorMessageBuffer(tempStrData);
			
		} else {
			
			if(tempStrData.substring(0, 2).equals("ED")) { 
			
				RunActivity.IsError = true;
			}
		}
	}
	
	public void BoardMessageBuffer(String tempData) {
		
		int tempHead;
		
		tempHead = (BoardMsgHead + 1) % UART_RX_MASK;
		
		if(BoardMsgTail != tempHead) {
			
			BoardMsgBuffer[tempHead] = tempData;
			BoardMsgHead = tempHead;
		}
	}
	
	public String BoardMessageOutput() {
		
		int tempTail;
		
		if(BoardMsgHead == BoardMsgTail) return Hardware.NO_RESPONSE;
		
		tempTail = (BoardMsgTail + 1) % UART_RX_MASK;
		BoardMsgTail = tempTail;

		return BoardMsgBuffer[tempTail];
	}
	
	public void SensorMessageBuffer(String tempData) {
		
		int tempHead;
		
		tempHead = (SensorMsgHead + 1) % UART_RX_MASK;
		
		if(SensorMsgTail != tempHead) {
			
			SensorMsgBuffer[tempHead] = tempData;
			SensorMsgHead = tempHead;
		}
		
		tempHead = (pCMsgHead + 1) % UART_RX_MASK;
	}
	
	public String SensorMessageOutput() {
		
		int tempTail;

		if(SensorMsgHead == SensorMsgTail) return Hardware.NO_RESPONSE;
			
		tempTail = (SensorMsgTail + 1) % UART_RX_MASK;
		SensorMsgTail = tempTail;
		
		return SensorMsgBuffer[tempTail];
	}
	
	public class RxUART2fromPC extends Thread { // Receiving from a board
		
		public void run() {
			
			while(!isInterrupted()) {
				
				int size;
				
				try {

					if(PrinterFD != null) {

						uART2InputBuffer = new byte[BOARD_INPUT_BUFFER];
						size = PrinterFIS.read(uART2InputBuffer);
						
						UART2DataReceive(size);
					}
					
				} catch (IOException e) {
					
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	public void stopRxUART2fromPC() {
		
		mRxUART2fromPC.interrupt();
	}
	
	public synchronized void UART2DataReceive(int size) {
		
		int tempHead;
		
		tempHead = (uART2InputHead + 1) % BOARD_INPUT_MASK;
		
		if((tempHead != uART2InputTail) && (size < 8)) { // because size of uART2RxBuffer[][] is 8, i+1(=size) < 8
			
			uART2RxBuffer[tempHead][0] = (byte) size; // input length of data
			for(int i = 0; i < size; i++) uART2RxBuffer[tempHead][i+1] = uART2InputBuffer[i];
			
			uART2InputHead = tempHead;
		}
	}
	
	public byte[] UART2InputData() {
		
		int tempTail;
		
		if(uART2InputTail != uART2InputHead) {
			
			tempTail = (uART2InputTail + 1) % BOARD_INPUT_MASK;
			uART2InputTail = tempTail;
				
			return uART2RxBuffer[tempTail];	
		
		} else return null;
	}
	
	public void UART2RxData() {
		
		UART2RxData mUART2RxData = new UART2RxData();
		mUART2RxData.start();
	}
	
	public class UART2RxData extends Thread {
		
		public void run() {
			
			byte[] tempBuffer;
			byte tempData;
			
			tempBuffer = UART2InputData();
			
			if(tempBuffer != null) {
				
				for(int i = 0; i < tempBuffer[0]; i++) { // tempBuffer[0] : length of buffer data
					
					tempData = tempBuffer[i+1];

//					Log.w("UART2RxData", "tempData : " + Integer.toHexString((int) (0X000000ff & tempData)));
					if(uART2RxFlag) {
						
						if(tempData == ETX) {
							
							UART2MessageForm(uART2RxDataList);
							uART2RxFlag = false;
						
						} else uART2RxDataList.add(uART2RxIdx++, tempData);
						
					} else if(tempData == STX) {
						
						uART2RxDataList = new ArrayList<Byte>();
						uART2RxIdx = 0;
						uART2RxFlag = true;
					}
				}
			}
		}
	}
	
	private ArrayList<Byte> stuffingUART2TxData(ArrayList<Byte> data) {
		
		ArrayList<Byte> stuffingData = new ArrayList<Byte>();
		byte tempData;
		int idx = 0;
		
		for(int i = 0; i < data.size(); i++) {
			
			tempData = data.get(i);
			
			if(tempData == 0x7D || tempData == 0x7E || tempData == 0x7F) {
				
				stuffingData.add(idx++, (byte) 0x7D);
				
				tempData = (byte) (tempData ^ 0x20);
			}
//			Log.w("stuffingUART2TxData", "data : " + (tempData & 0x00ff));
			stuffingData.add(idx++, tempData);
		}
		
		return stuffingData;
	}
	
	private byte[] destuffingUART2RxData(ArrayList<Byte> data) {
		
		byte[] destuffingData = new byte[data.size()];
		byte tempData;
		int idx = 0;
		
		for(int i = 0; i < data.size(); i++) {
			
			tempData = data.get(i);
			
			if(tempData == 0x5D || tempData == 0x5E || tempData == 0x5F) {
				
				if((i != 0) && (data.get(i-1) == 0x7D)) {
			
					tempData = (byte) (tempData ^ 0x20);
					idx = idx - 1;
				}
			}
			
			destuffingData[idx++] = tempData;
		}
		
		return destuffingData;
	}
	
	private void UART2MessageForm(ArrayList<Byte> data) {
		
		byte[] tempData = new byte[data.size()];
			
		tempData = destuffingUART2RxData(data);
		
		if(tempData[0] == 0x01) { // 0x01 : device code of A1Care Analyzer
			
			int dataLen = (int) (tempData[2] << 8) + (int) tempData[3],
				crcRefLen = dataLen + 4;
			
//			Log.w("UART2MessageForm", "dataLen : " + dataLen + ", crcRefLen : " + crcRefLen);
			
			char crc = getCRCtoChar(tempData[crcRefLen], tempData[crcRefLen+1]);
			
			if(CRCCheck(tempData, crcRefLen, crc)) PCMessageBuffer(tempData, crcRefLen);
			else startUART2toPC(PC_QC_CAL, getCRCErrorforTx()); // transmit CRC error
		
		} else startUART2toPC(PC_QC_CAL, getDeviceErrorforTx()); // transmit device code error
	}
	
	private ArrayList<Byte> setCRC(ArrayList<Byte> dataList) {
		
		int len = dataList.size();
		byte[] tempData = new byte[len];
		
		for(int i = 0; i < dataList.size(); i++) tempData[i] = dataList.get(i);
//		Log.w("setCRC", "size : " + len);
		char crc = CRCGenerate(tempData, tempData.length);
		
		dataList.add(len, (byte) ((crc >> 8) & 0xff)); // high bit of CRC
		dataList.add(len+1, (byte) (crc & 0xff)); // low bit of CRC
		
		return dataList;
	}
	
	private char getCRCtoChar(byte hData, byte lData) {
		
		return (char) (((0xffff & hData) << 8) + (0x00ff & lData)); // hData : received high bit of CRC, lData : received low bit of CRC
	}
	
	private char CRCGenerate(byte[] pDataBuffer, long usDataLen) {
		
		char crc16;
        int i;

        crc16 = CRC_16_SEED;
       
        for (i = 0; i < usDataLen; i++) {
        	
        	 crc16 = (char) (CRC16_TABLE[((crc16 >> 8) ^ (pDataBuffer[i] & 0x00ff))] ^ (crc16 << 8));
        }
        
        return (crc16);
    }

	private boolean CRCCheck(byte[] ptr, int len, char crc) {
		
		char crc16 = 0xFFFF;
		
		if (ptr == null) return false;
		
		if (len <= 0) return false;
		
		crc16 = CRCGenerate(ptr, len);
		
//		Log.w("CRCCheck", "crc16 : " + (int) crc16 + ", crc : " + (int) crc);
		
		if ((int) crc == (int) crc16) return true;
	
		return false;
	}
	
	public void PCMessageBuffer(byte[] data, int len) {
		
		int tempHead,
			lenHead = 0,
			tempDataLen;
		byte[] tempData = new byte[BOARD_INPUT_MASK];
		
		while(len > 0) { // run if data remain
		
//			Log.w("PCMessageBuffer", "len : " + len);
			
			if(len >= 8) tempDataLen = 8; // length of maximum data buffer : 8
			else tempDataLen = len;
			
			for(int i = 0; i < tempDataLen; i++) {
				
				tempData[i] = data[lenHead++];
//				Log.w("PCMessageBuffer", "tempData : " + tempData[i] + ", i : " + i);
			}
			
			len -= 8;
			
			tempHead = (pCMsgHead + 1) % UART_RX_MASK;
			
			while(pCMsgTail == tempHead) SerialPort.Sleep(10);
				
			for(int i = 0; i < tempDataLen; i++) pCMsgBuffer[tempHead][i] = tempData[i];
			pCMsgHead = tempHead;	
		}
	}
	
	private byte[] PCMessageOutput() {
		
		int tempTail;
		
		if(pCMsgHead == pCMsgTail) return Hardware.NO_RESPONSE.getBytes();
		
		tempTail = (pCMsgTail + 1) % UART_RX_MASK;
		pCMsgTail = tempTail;
		
		return pCMsgBuffer[tempTail];
	}
	
	public ArrayList<Byte> getPCMsg() {
		
		ArrayList<Byte> pCMsgList = new ArrayList<Byte>();
		int len,
			tempHead = 0,
			tempDataLen;
		byte[] tempData = new byte[BOARD_INPUT_MASK];
	
		tempData = PCMessageOutput();
		
		if(!new String(tempData).equals(Hardware.NO_RESPONSE)) {
			
			len = (int) (tempData[2] << 8) + (int) tempData[3] + 4; // len : length of protocol data
			
//			Log.w("getPCMsg", "len : " + len);
			
			for(int i = 0; i < BOARD_INPUT_MASK; i++) {
				
				pCMsgList.add(tempHead++, tempData[i]);
//				Log.w("getPCMsg", "tempData : " + tempData[i] + ", i : " + i);
			}
			
			len -= 8; // len is reduced length of processed 8 bytes
			
			while(len > 0) {
				
				tempData = PCMessageOutput();
				
				if(tempData != Hardware.NO_RESPONSE.getBytes()) {
					
//					Log.w("getPCMsg", "len : " + len);
					if(len >= 8) tempDataLen = 8; // length of maximum data buffer : 8
					else tempDataLen = len;
					
					for(int i = 0; i < tempDataLen; i++) {
						
						pCMsgList.add(tempHead++, tempData[i]);
//						Log.w("getPCMsg", "tempData : " + tempData[i] + ", i : " + i);
					}
					
					len -= 8;
				}
								
				SerialPort.Sleep(10);
			}
			
			return pCMsgList;
		}
		
		pCMsgList.add(0, (byte) 0);
		
		return pCMsgList;
	}
	
	public class BarcodeRxThread extends Thread { // Receiving from a barcode sensor
		
		public void run() {

			while(!isInterrupted()) {
				
				int size;

				try {
					
					if(BarcodeFIS != null) {
						
						BarcodeRxBuffer = new byte[BARCODE_RX_BUFFER_SIZE];
						size = BarcodeFIS.read(BarcodeRxBuffer);
						
//						Log.w("BarcodeRxThread", "size : " + size + " data : " + new String(BarcodeRxBuffer));
						
						if(size > 0) {
							
							BarcodeDataReceive(size);
						}
						
					} else {
						
						return;
					}
				} catch (IOException e) {
					
					e.printStackTrace();
					return;
				}
			}
		}
	}
		
	public synchronized void BarcodeDataReceive(int size) { // making a buffer of received data from a barcode sensor
		
		byte maxIndex;
		
		if(BarcodeReadStart == false) {
		
			BarcodeReadStart = true;
			BarcodeBufIndex = 0;
			BarcodeAppendBuffer[BarcodeBufCnt] = new byte[BARCODE_BUFFER_INDEX_SIZE];
		}
		
		for(int i = 0; i < size; i++) {

			BarcodeAppendBuffer[BarcodeBufCnt][BarcodeBufIndex++] = BarcodeRxBuffer[i]; // bufCnt : number of each buffer, bufIndex : bit index of one buffer
		}	
		
		if(ActionActivity.BarcodeQCCheckFlag) maxIndex = A1C_MAX_BUFFER_INDEX;
		else maxIndex = A1C_QC_MAX_BUFFER_INDEX;
//		Log.w("BarcodeDataReceive", "data length : " + BarcodeBufIndex);
		if(BarcodeBufIndex > maxIndex | BarcodeBufIndex < A1C_MIN_BUFFER_INDEX) {
			
			ActionActivity.IsCorrectBarcode = false;
			ActionActivity.BarcodeCheckFlag = true;
			BarcodeReadStart = false;
		
		} else {
			
			if(BarcodeRxBuffer[size-2] == CR && BarcodeRxBuffer[size-1] == LF) { // Whether or not end bit

				BarcodeReadStart = false;
				
				BarcodeDataAppend(BarcodeBufCnt, BarcodeBufIndex);
				
				BarcodeBufCnt++;
				if(BarcodeBufCnt == BARCODE_BUFFER_CNT_SIZE) BarcodeBufCnt = 0;
			}
		}
	}
	
	public synchronized void BarcodeDataAppend(byte num, byte len) {
		
		try {
			
			final StringBuffer barcodeReception = new StringBuffer();
			
			barcodeReception.append(new String(BarcodeAppendBuffer[num], 0, len));
		
			mBarcode = new Barcode();
			mBarcode.BarcodeCheck(barcodeReception);
			
		} catch(ArrayIndexOutOfBoundsException e) {
			
			e.printStackTrace();
			BarcodeBufCnt = 0;
			return;
		}		
	}
	
	public class HHBarcodeRxThread extends Thread { // Receiving from a barcode sensor
		
		public void run() {

			while(MainTimer.ExtDeviceBarcode == MainTimer.FILE_OPEN) {
						
				int size;
				
				try {
					
					if(HHBarcodeFIS != null) {
						
						HHBarcodeRxBuffer = new byte[BARCODE_RX_BUFFER_SIZE];
						size = HHBarcodeFIS.read(HHBarcodeRxBuffer);
						
						if(size > 0) {
								
							HHBarcodeDataReceive(size);

						} else {
							
							HHBarcodeFIS.close();
							close();
						}
						
					} else {
						
						return;
					}
					
				} catch (IOException e) {
					
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	public synchronized void HHBarcodeDataReceive(int size) { // making a buffer of received data from a barcode sensor
		
		if(HHBarcodeReadStart == false) {
		
			HHBarcodeReadStart = true;
			HHBarcodeBufIndex = 0;
			HHBarcodeAppendBuffer[BarcodeBufCnt] = new byte[BARCODE_BUFFER_INDEX_SIZE];
		}
		
		for(int i = 0; i < size; i++) {

			HHBarcodeAppendBuffer[HHBarcodeBufCnt][HHBarcodeBufIndex++] = HHBarcodeRxBuffer[i]; // bufCnt : number of each buffer, bufIndex : bit index of one buffer
		}
		
		if(HHBarcodeRxBuffer[size-1] == CR) { // Whether or not end bit

			HHBarcodeReadStart = false;
			
			HHBarcodeDataAppend(HHBarcodeBufCnt, HHBarcodeBufIndex);
			
			HHBarcodeBufCnt++;
			if(HHBarcodeBufCnt == BARCODE_BUFFER_CNT_SIZE) HHBarcodeBufCnt = 0;
		}
	}
	
	public synchronized void HHBarcodeDataAppend(byte num, byte len) {
		
		try {
			
			final StringBuffer HHbarcodeReception = new StringBuffer();
			
			HHbarcodeReception.append(new String(HHBarcodeAppendBuffer[num], 0, len));
			
			if(MainTimer.layoutid == R.id.resultlayout) {
				
				Handler mHandler = new Handler(Looper.getMainLooper());

				mHandler.postDelayed(new Runnable() {

					public void run() {
	
						mResultActivity = new ResultActivity();
						mResultActivity.PatientIDDisplay(HHbarcodeReception);
					}
				}, 0);
			}
				
		} catch(ArrayIndexOutOfBoundsException e) {
			
			e.printStackTrace();
			HHBarcodeBufCnt = 0;
			return;
		}		
	}
	
	public void BoardSerialInit() {
		
		BoardFD = open("/dev/ttySAC3", 9600, 0);
	}
	
	public void BoardRxStart() {
		
		BoardFIS = new FileInputStream(BoardFD);				
		mBoardRxThread = new BoardRxThread();
		mBoardRxThread.start();
	}
	
	public synchronized void BoardTx(String str, CtrTarget trg) {
		
		switch(trg) {

		case MotorSet	:	
			mBoardTxThread = new BoardTxThread(str, CtrTarget.MotorSet);
			mBoardTxThread.start();
			break;
										
		default			: 
			mBoardTxThread = new BoardTxThread(str, trg);
			mBoardTxThread.start();
			break; 
		}
	}
	
	public void PrinterSerialInit() {
	
		if(HomeActivity.ANALYZER_DEVICE == HomeActivity.PP) PrinterFD = open("/dev/ttySAC2", 9600, 0); // PP
		else if(HomeActivity.ANALYZER_DEVICE == HomeActivity.ES) PrinterFD = open("/dev/ttySAC1", 9600, 0); // ES
		else PrinterFD = open("/dev/ttySAC2", 9600, 0); // PP
	}
	
	public void startRxUART2() {
		
		PrinterFIS = new FileInputStream(PrinterFD);
		mRxUART2fromPC = new RxUART2fromPC();
		mRxUART2fromPC.start();
	}
	
	public void PrinterTxStart(Activity activity, Context context, byte mode, ArrayList<String> txtList) {
		
		pPrinterTxThread = new PrinterTxThread(activity, context, mode, getPrintData(txtList));
		pPrinterTxThread.start();
	}
	
	public void startUART2toPC(byte mode, ArrayList<Byte> txDataList) {
		
		TxUART2toPC mTxUART2toPC = new TxUART2toPC(mode, setTxPkg(stuffingUART2TxData(setCRC(txDataList))));
		mTxUART2toPC.start();
	}
	
	private byte[] setTxPkg(ArrayList<Byte> dataList) {

		int len = dataList.size();
		byte[] txData = new byte[len+2];

		txData[0] = STX; // STX + dataList + ETX : packet of communication protocol
		
		for(int i = 0; i < len; i++) txData[i+1] = dataList.get(i);

		txData[len+1] = ETX;
		
		return txData;
	}
	
	public ArrayList<Byte> getHWSNforTx() {
		
		ArrayList<Byte> txDataList = new ArrayList<Byte>();
		int len = AboutModel.HWSN.getBytes().length;
		
		txDataList.add(0, (byte) 0x01); // device code
		txDataList.add(1, (byte) (Hardware.PC_QC_CONNECT+0x01)); // message type
		txDataList.add(2, (byte) ((len >> 8) & 0xff)); // high bit of length of data
		txDataList.add(3, (byte) (len & 0xff)); // low bit of length of data
		
		for(int i = 0; i < len; i++) txDataList.add(i+4, AboutModel.HWSN.getBytes()[i]);
		
		return txDataList;
	}
	
	private ArrayList<Byte> getCRCErrorforTx() {
		
		ArrayList<Byte> txDataList = new ArrayList<Byte>();
			
		txDataList.add(0, (byte) 0x01); // device code
		txDataList.add(1, (byte) 0xff); // message type
		txDataList.add(2, (byte) 0x00); // high bit of length of data
		txDataList.add(3, (byte) 0x01); // low bit of length of data
		txDataList.add(4, (byte) 0x01);
		
		return txDataList;
	}

	public ArrayList<Byte> getMsgTypeErrorforTx() {
		
		ArrayList<Byte> txDataList = new ArrayList<Byte>();
		
		txDataList.add(0, (byte) 0x01); // device code
		txDataList.add(1, (byte) 0xff); // message type
		txDataList.add(2, (byte) 0x00); // high bit of length of data
		txDataList.add(3, (byte) 0x01); // low bit of length of data
		txDataList.add(4, (byte) 0x02);
		
		return txDataList;
	}

	private ArrayList<Byte> getDeviceErrorforTx() {
		
		ArrayList<Byte> txDataList = new ArrayList<Byte>();
		
		txDataList.add(0, (byte) 0x01); // device code
		txDataList.add(1, (byte) 0xff); // message type
		txDataList.add(2, (byte) 0x00); // high bit of length of data
		txDataList.add(3, (byte) 0x01); // low bit of length of data
		txDataList.add(4, (byte) 0x03);
		
		return txDataList;
	}

	public void BarcodeSerialInit() {

		if(HomeActivity.ANALYZER_DEVICE == HomeActivity.PP) BarcodeFD = open("/dev/ttySAC1", 115200, 0); // PP
		else if(HomeActivity.ANALYZER_DEVICE == HomeActivity.ES) BarcodeFD = open("/dev/ttySAC2", 115200, 0); // PP
		else BarcodeFD = open("/dev/ttySAC1", 115200, 0); // PP
	}

	public void BarcodeRxStart() {
		
		BarcodeFIS = new FileInputStream(BarcodeFD);
		bBarcodeRxThread = new BarcodeRxThread();
		bBarcodeRxThread.start();
	}
	
	public void HHBarcodeSerialInit() {
		
		try {

            Runtime.getRuntime().exec("ssu -c busybox chmod 0777 /dev/ttyACM0").waitFor();
    
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mFd = open("/dev/ttyACM0", 9600, 0);
	}
	
	public void HHBarcodeRxStart() {
		
		try {
			
			HHBarcodeFIS = new FileInputStream(mFd);
			hBarcodeRxThread = new HHBarcodeRxThread();
			hBarcodeRxThread.start();
			
			MainTimer.ExtDeviceBarcode = MainTimer.FILE_OPEN;
				
		} catch(NullPointerException e) {
			
			MainTimer.ExtDeviceBarcode = MainTimer.FILE_NOT_OPEN;
		}
	}
	
	public static void Sleep(int t) { // t : msec
		
		try {
			
			Thread.sleep(t);
			
		} catch(InterruptedException e) {
			
			e.printStackTrace();			
			return;
		}
	}
	
	static {
	
		System.loadLibrary("serial_port");
	}
	
	/*JNI*/
	public native static FileDescriptor open(String path, int baudrate, int flags);
	public native void close();
}
