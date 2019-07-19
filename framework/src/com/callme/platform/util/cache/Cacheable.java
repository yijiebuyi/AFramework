package com.callme.platform.util.cache;

public interface Cacheable extends java.io.Serializable {
	public static final byte TYPE_FILE = 1;

	/**
	 * 返回对象类型
	 * 
	 * @return
	 */
	public int getCachedType();

	/**
	 * 返回对象的大概尺寸
	 * 
	 * 缓存对象尺寸用来估算缓存系统占用空间大小
	 */
	public int getCachedSize();

	/**
	 * 缓存对象自清理回收
	 */
	public void recycle();

	/**
	 * 序列化
	 */
	public byte[] serialize();
}
