/**
 */
package bibtex;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Authored Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bibtex.AuthoredEntry#getAuthors <em>Authors</em>}</li>
 * </ul>
 *
 * @see bibtex.BibtexPackage#getAuthoredEntry()
 * @model abstract="true"
 * @generated
 */
public interface AuthoredEntry extends BibTeXEntry
{
	/**
	 * Returns the value of the '<em><b>Authors</b></em>' containment reference list.
	 * The list contents are of type {@link bibtex.Author}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Authors</em>' containment reference list.
	 * @see bibtex.BibtexPackage#getAuthoredEntry_Authors()
	 * @model type="bibtex.Author" containment="true" required="true" ordered="false"
	 * @generated
	 */
	EList getAuthors();

} // AuthoredEntry
