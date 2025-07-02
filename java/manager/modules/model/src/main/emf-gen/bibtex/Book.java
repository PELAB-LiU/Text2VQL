/**
 */
package bibtex;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Book</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bibtex.Book#getPublisher <em>Publisher</em>}</li>
 * </ul>
 *
 * @see bibtex.BibtexPackage#getBook()
 * @model
 * @generated
 */
public interface Book extends AuthoredEntry, DatedEntry, TitledEntry
{
	/**
	 * Returns the value of the '<em><b>Publisher</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publisher</em>' attribute.
	 * @see #setPublisher(Object)
	 * @see bibtex.BibtexPackage#getBook_Publisher()
	 * @model unique="false" dataType="primitivetypes.String" required="true" ordered="false"
	 * @generated
	 */
	Object getPublisher();

	/**
	 * Sets the value of the '{@link bibtex.Book#getPublisher <em>Publisher</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Publisher</em>' attribute.
	 * @see #getPublisher()
	 * @generated
	 */
	void setPublisher(Object value);

} // Book
