package droid64.d64;

public class ByteIterator {

	private byte[] data;
	private int position;

	public ByteIterator(byte[] data) {
		this.data = data;
		this.position=0;
	}

	public boolean hasNext() {
		return position < data.length;
	}

	public boolean hasNextInt16() {
		return position + 1 < data.length;
	}

	public int nextInt8() {
		return data[position++] & 0xff;
	}

	public int nextInt16() {
		return (data[position++] & 0xff) | ((data[position++] << 8) & 0xff00);
	}

	public void skip(int count) {
		position += count;
	}

}
