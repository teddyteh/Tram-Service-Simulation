package models;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	protected byte[] data = null;

	protected int length = 0;

	public void marshal(RPCMessage rpcMessage) {
		int buffSize = rpcMessage.lenInBytes();
		ByteBuffer bb = ByteBuffer.allocate(buffSize);

		int index = 0;

		short typeNo = rpcMessage.getType();
		bb.putShort(0, typeNo);
		index += 2;

		bb.putLong(index, rpcMessage.getTransactionId());
		index += 8;

		bb.putLong(index, rpcMessage.getRPCId());
		index += 8;

		bb.putLong(index, rpcMessage.getRequestId());
		index += 8;

		bb.putShort(index, rpcMessage.getProcedure());
		index += 2;

		bb.putShort(index, rpcMessage.getStatus());
		index += 2;

		String csvData = rpcMessage.getCsvData();
		if (csvData != null) {
			for (int i = 0; i < csvData.length(); i++, index += 2) {
				bb.putChar(index, csvData.charAt(i));
			}
		}

		data = bb.array();
	}

	public RPCMessage unMarshall() {
		RPCMessage message = new RPCMessage();
		ByteBuffer bb = ByteBuffer.wrap(data);

		int index = 0;

		message.setType(bb.getShort(index));
		index += 2;

		message.setTransactionId(bb.getLong(index));
		index += 8;

		message.setRPCId(bb.getLong(index));
		index += 8;

		message.setRequestId(bb.getLong(index));
		index += 8;

		message.setProcedure(bb.getShort(index));
		index += 2;

		message.setStatus(bb.getShort(index));
		index += 2;

		StringBuffer sb = new StringBuffer();
		for (; index < bb.array().length; index += 2) {
			char bbChar = bb.getChar(index);
			sb.append(bbChar);
		}
		String data = sb.toString();
		message.setCsvData(data);

		return message;
	}
}
