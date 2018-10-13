package uk.ac.warwick.dcs.sherlock.deprecated.services.detection;

class Tuple <K, V>{
	private K key;
	private V value;
	public Tuple(K  k, V v){
		this.key = k;
		this.value = v;
	}
	public K getKey(){
		return this.key;
	}
	public V getValue(){
		return this.value;
	}
	public void setKey(K k){
		this.key = k;
	}
	public void setValue(V v){
		this.value = v;
	}
	public boolean same(Tuple<K,V> t2) {
		return (this.key.equals(t2.getKey()) && this.value.equals(t2.getValue()));
	}
	public Tuple clone(){
		return new Tuple<K,V>(this.key, this.value);
	}
	@Override
	public String toString(){
		return String.valueOf(this.key) + ", " + String.valueOf(this.value);
    }
}