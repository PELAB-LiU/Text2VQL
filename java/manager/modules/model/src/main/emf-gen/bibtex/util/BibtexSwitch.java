/**
 */
package bibtex.util;

import bibtex.*;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see bibtex.BibtexPackage
 * @generated
 */
public class BibtexSwitch
{
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static BibtexPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BibtexSwitch()
	{
		if (modelPackage == null)
		{
			modelPackage = BibtexPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public Object doSwitch(EObject theEObject)
	{
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(EClass theEClass, EObject theEObject)
	{
		if (theEClass.eContainer() == modelPackage)
		{
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else
		{
			List eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch((EClass)eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(int classifierID, EObject theEObject)
	{
		switch (classifierID)
		{
			case BibtexPackage.BIB_TE_XFILE:
			{
				BibTeXFile bibTeXFile = (BibTeXFile)theEObject;
				Object result = caseBibTeXFile(bibTeXFile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.AUTHOR:
			{
				Author author = (Author)theEObject;
				Object result = caseAuthor(author);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.BIB_TE_XENTRY:
			{
				BibTeXEntry bibTeXEntry = (BibTeXEntry)theEObject;
				Object result = caseBibTeXEntry(bibTeXEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.AUTHORED_ENTRY:
			{
				AuthoredEntry authoredEntry = (AuthoredEntry)theEObject;
				Object result = caseAuthoredEntry(authoredEntry);
				if (result == null) result = caseBibTeXEntry(authoredEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.DATED_ENTRY:
			{
				DatedEntry datedEntry = (DatedEntry)theEObject;
				Object result = caseDatedEntry(datedEntry);
				if (result == null) result = caseBibTeXEntry(datedEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.TITLED_ENTRY:
			{
				TitledEntry titledEntry = (TitledEntry)theEObject;
				Object result = caseTitledEntry(titledEntry);
				if (result == null) result = caseBibTeXEntry(titledEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.BOOK_TITLED_ENTRY:
			{
				BookTitledEntry bookTitledEntry = (BookTitledEntry)theEObject;
				Object result = caseBookTitledEntry(bookTitledEntry);
				if (result == null) result = caseBibTeXEntry(bookTitledEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.ARTICLE:
			{
				Article article = (Article)theEObject;
				Object result = caseArticle(article);
				if (result == null) result = caseAuthoredEntry(article);
				if (result == null) result = caseDatedEntry(article);
				if (result == null) result = caseTitledEntry(article);
				if (result == null) result = caseBibTeXEntry(article);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.TECH_REPORT:
			{
				TechReport techReport = (TechReport)theEObject;
				Object result = caseTechReport(techReport);
				if (result == null) result = caseAuthoredEntry(techReport);
				if (result == null) result = caseDatedEntry(techReport);
				if (result == null) result = caseTitledEntry(techReport);
				if (result == null) result = caseBibTeXEntry(techReport);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.UNPUBLISHED:
			{
				Unpublished unpublished = (Unpublished)theEObject;
				Object result = caseUnpublished(unpublished);
				if (result == null) result = caseAuthoredEntry(unpublished);
				if (result == null) result = caseTitledEntry(unpublished);
				if (result == null) result = caseBibTeXEntry(unpublished);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.MANUAL:
			{
				Manual manual = (Manual)theEObject;
				Object result = caseManual(manual);
				if (result == null) result = caseTitledEntry(manual);
				if (result == null) result = caseBibTeXEntry(manual);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.PROCEEDINGS:
			{
				Proceedings proceedings = (Proceedings)theEObject;
				Object result = caseProceedings(proceedings);
				if (result == null) result = caseDatedEntry(proceedings);
				if (result == null) result = caseTitledEntry(proceedings);
				if (result == null) result = caseBibTeXEntry(proceedings);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.IN_PROCEEDINGS:
			{
				InProceedings inProceedings = (InProceedings)theEObject;
				Object result = caseInProceedings(inProceedings);
				if (result == null) result = caseProceedings(inProceedings);
				if (result == null) result = caseAuthoredEntry(inProceedings);
				if (result == null) result = caseBookTitledEntry(inProceedings);
				if (result == null) result = caseDatedEntry(inProceedings);
				if (result == null) result = caseTitledEntry(inProceedings);
				if (result == null) result = caseBibTeXEntry(inProceedings);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.BOOKLET:
			{
				Booklet booklet = (Booklet)theEObject;
				Object result = caseBooklet(booklet);
				if (result == null) result = caseDatedEntry(booklet);
				if (result == null) result = caseBibTeXEntry(booklet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.BOOK:
			{
				Book book = (Book)theEObject;
				Object result = caseBook(book);
				if (result == null) result = caseAuthoredEntry(book);
				if (result == null) result = caseDatedEntry(book);
				if (result == null) result = caseTitledEntry(book);
				if (result == null) result = caseBibTeXEntry(book);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.IN_COLLECTION:
			{
				InCollection inCollection = (InCollection)theEObject;
				Object result = caseInCollection(inCollection);
				if (result == null) result = caseBook(inCollection);
				if (result == null) result = caseBookTitledEntry(inCollection);
				if (result == null) result = caseAuthoredEntry(inCollection);
				if (result == null) result = caseDatedEntry(inCollection);
				if (result == null) result = caseTitledEntry(inCollection);
				if (result == null) result = caseBibTeXEntry(inCollection);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.IN_BOOK:
			{
				InBook inBook = (InBook)theEObject;
				Object result = caseInBook(inBook);
				if (result == null) result = caseBook(inBook);
				if (result == null) result = caseAuthoredEntry(inBook);
				if (result == null) result = caseDatedEntry(inBook);
				if (result == null) result = caseTitledEntry(inBook);
				if (result == null) result = caseBibTeXEntry(inBook);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.MISC:
			{
				Misc misc = (Misc)theEObject;
				Object result = caseMisc(misc);
				if (result == null) result = caseBibTeXEntry(misc);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.THESIS_ENTRY:
			{
				ThesisEntry thesisEntry = (ThesisEntry)theEObject;
				Object result = caseThesisEntry(thesisEntry);
				if (result == null) result = caseAuthoredEntry(thesisEntry);
				if (result == null) result = caseDatedEntry(thesisEntry);
				if (result == null) result = caseTitledEntry(thesisEntry);
				if (result == null) result = caseBibTeXEntry(thesisEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.PH_DTHESIS:
			{
				PhDThesis phDThesis = (PhDThesis)theEObject;
				Object result = casePhDThesis(phDThesis);
				if (result == null) result = caseThesisEntry(phDThesis);
				if (result == null) result = caseAuthoredEntry(phDThesis);
				if (result == null) result = caseDatedEntry(phDThesis);
				if (result == null) result = caseTitledEntry(phDThesis);
				if (result == null) result = caseBibTeXEntry(phDThesis);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BibtexPackage.MASTER_THESIS:
			{
				MasterThesis masterThesis = (MasterThesis)theEObject;
				Object result = caseMasterThesis(masterThesis);
				if (result == null) result = caseThesisEntry(masterThesis);
				if (result == null) result = caseAuthoredEntry(masterThesis);
				if (result == null) result = caseDatedEntry(masterThesis);
				if (result == null) result = caseTitledEntry(masterThesis);
				if (result == null) result = caseBibTeXEntry(masterThesis);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bib Te XFile</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bib Te XFile</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseBibTeXFile(BibTeXFile object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Author</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Author</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseAuthor(Author object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Bib Te XEntry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Bib Te XEntry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseBibTeXEntry(BibTeXEntry object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Authored Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Authored Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseAuthoredEntry(AuthoredEntry object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Dated Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Dated Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseDatedEntry(DatedEntry object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Titled Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Titled Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseTitledEntry(TitledEntry object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Book Titled Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Book Titled Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseBookTitledEntry(BookTitledEntry object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Article</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Article</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseArticle(Article object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Tech Report</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Tech Report</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseTechReport(TechReport object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Unpublished</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Unpublished</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseUnpublished(Unpublished object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Manual</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Manual</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseManual(Manual object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Proceedings</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Proceedings</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseProceedings(Proceedings object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>In Proceedings</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>In Proceedings</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseInProceedings(InProceedings object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Booklet</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Booklet</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseBooklet(Booklet object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Book</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Book</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseBook(Book object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>In Collection</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>In Collection</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseInCollection(InCollection object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>In Book</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>In Book</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseInBook(InBook object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Misc</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Misc</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseMisc(Misc object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Thesis Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Thesis Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseThesisEntry(ThesisEntry object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Ph DThesis</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Ph DThesis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object casePhDThesis(PhDThesis object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Master Thesis</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Master Thesis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseMasterThesis(MasterThesis object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public Object defaultCase(EObject object)
	{
		return null;
	}

} //BibtexSwitch
