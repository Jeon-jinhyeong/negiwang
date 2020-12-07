package com.negiwang.android.helper;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProgressiveEntity implements HttpEntity {
	HttpEntity mHttpEntity = null;

	public ProgressiveEntity(MultipartEntityBuilder builder) {
		this.mHttpEntity = builder.build();
	}

	@Override
	public void consumeContent() throws IOException {
		mHttpEntity.consumeContent();
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return mHttpEntity.getContent();
	}

	@Override
	public Header getContentEncoding() {
		return mHttpEntity.getContentEncoding();
	}

	@Override
	public long getContentLength() {
		return mHttpEntity.getContentLength();
	}

	@Override
	public Header getContentType() {
		return mHttpEntity.getContentType();
	}

	@Override
	public boolean isChunked() {
		return mHttpEntity.isChunked();
	}

	@Override
	public boolean isRepeatable() {
		return mHttpEntity.isRepeatable();
	}

	@Override
	public boolean isStreaming() {
		return mHttpEntity.isStreaming();
	} // CONSIDER put a _real_ delegator into here!

	@Override
	public void writeTo(OutputStream outstream) throws IOException {

		class ProxyOutputStream extends FilterOutputStream {
			/**
			 * @author Stephen Colebourne
			 */

			public ProxyOutputStream(OutputStream proxy) {
				super(proxy);
			}

			public void write(int idx) throws IOException {
				out.write(idx);
			}

			public void write(byte[] bts) throws IOException {
				out.write(bts);
			}

			public void write(byte[] bts, int st, int end) throws IOException {
				out.write(bts, st, end);
			}

			public void flush() throws IOException {
				out.flush();
			}

			public void close() throws IOException {
				out.close();
			}
		} // CONSIDER import this class (and risk more Jar File Hell)

		class ProgressiveOutputStream extends ProxyOutputStream {
			public ProgressiveOutputStream(OutputStream proxy) {
				super(proxy);
			}

			public void write(byte[] bts, int st, int end) throws IOException {

				out.write(bts, st, end);
			}
		}

		mHttpEntity.writeTo(new ProgressiveOutputStream(outstream));
	}

}