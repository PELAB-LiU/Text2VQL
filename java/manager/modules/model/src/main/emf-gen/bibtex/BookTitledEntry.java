/**
 */
package bibtex;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Book Titled Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bibtex.BookTitledEntry#getBooktitle <em>Booktitle</em>}</li>
 * </ul>
 *
 * @see bibtex.BibtexPackage#getBookTitledEntry()
 * @model abstract="true"
 * @generated
 */
public interface BookTitledEntry extends BibTeXEntry
{
	/**
	 * Returns the value of the '<em><b>Booktitle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Booktitle</em>' attribute.
	 * @see #setBooktitle(Object)
	 * @see bibtex.BibtexPackage#getBookTitledEntry_Booktitle()
	 * @model unique="false" dataType="primitivetypes.String" required="true" ordered="false"
	 * @generated
	 */
	Object getBooktitle();

	/**
	 * Sets the value of the '{@link bibtex.BookTitledEntry#getBooktitle <em>Booktitle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Booktitle</em>' attribute.
	 * @see #getBooktitle()
	 * @generated
	 */
	void setBooktitle(Object value);

} // BookTitledEntry
