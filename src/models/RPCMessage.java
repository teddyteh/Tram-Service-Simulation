package models;

import java.io.Serializable;

public class RPCMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final short REQUEST = 0;
	public static final short REPLY = 1;

	public enum MessageType {
		REQUEST, REPLY
	};

	private MessageType messageType;
	private long transactionId; /* transaction id */
	private long RPCId; /* Globally unique identifier */
	private long requestId; /* Client request message counter */
	private short procedureId; /*
								 * e.g.(1,2,3,4) 1 for retrieve next stop, 2 for
								 * next stop etc
								 */
	private String csv_data; /* data as comma separated values */
	private short status;

	public RPCMessage() {
		this.setStatus((short) -1);
	}

	public int lenInBytes() {
		int length;

		if (csv_data == null)
			length = 0;
		else
			length = csv_data.length();

		return 2 + 8 + 8 + 8 + 2 + (length * 2) + 2;
	}

	public short getType() {
		if (this.messageType.equals(MessageType.REQUEST)) {
			return REQUEST;
		} else if (this.messageType.equals(MessageType.REPLY)) {
			return REPLY;
		}

		return -1;
	}

	public void setType(short typeNo) {
		if (typeNo == REQUEST) {
			this.messageType = MessageType.REQUEST;
		} else if (typeNo == REPLY) {
			this.messageType = MessageType.REPLY;
		}
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getRPCId() {
		return RPCId;
	}

	public void setRPCId(long id) {
		this.RPCId = id;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public short getProcedure() {
		return procedureId;
	}

	public void setProcedure(short id) {
		procedureId = id;

		// Set message type
		if (id == 1 || id == 3) {
			this.setType(REQUEST);
		} else if (id == 2 || id == 4) {
			this.setType(REPLY);
		}
	}

	public String getCsvData() {
		return csv_data;
	}

	public void setCsvData(String data) {
		this.csv_data = data;
	}

	public String[] getData() {
		String[] data = csv_data.split(",");

		return data;
	}

	public void setData(String data[]) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < data.length; i++) {
			builder.append(data[i]);

			if (i != data.length - 1)
				builder.append(",");
		}

		this.csv_data = builder.toString();
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}
}
