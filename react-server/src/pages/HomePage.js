import React from "react";

// Import Bootstrap components
import Alert from 'react-bootstrap/Alert';
import Modal from 'react-bootstrap/Modal';
import Table from 'react-bootstrap/Table';

/**
 * Component representing the HomePage.
 */
class HomePage extends React.Component {

  /**
   * Lifecycle method - executed after the component is added to the DOM.
   */
  componentDidMount() {

    this.domainContext.focus();
  }

  /**
   * Data structure for table information.
   * @type {{columnNames: string[], body: Array}}
   */
  tableData = {

    columnNames: ['#'],
    body: []
  }

  /**
   * Component state with initial values.
   * @type {{isOpen: boolean, hideTable: boolean, hideNotFound: boolean, modalMsg: string, variant: string, excuteTime: number}}
   */
  state = {

    isOpen: false,
    hideTable: true,
    hideNotFound: true,
    modalMsg: "",
    variant: "danger",
    excuteTime: 0
  };

  /**
   * Perform domain search and fetch data from the server.
   */
  searchDomain = async () => {

    const domain = this.domainContext.value.trim();

    // Validation for empty domain
    if (!domain.replace(/\s/g, '').length) {

      this.setState({ isOpen: true, modalMsg: "Please enter domain!" });
      this.domainContext.classList.add('is-invalid');
      return;
    }

    // Validation for invalid domain format
    const regexp = /(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\.)+[a-z0-9][a-z0-9-]{0,61}[a-z0-9]/g;

    if (!regexp.test(domain)) {

      this.setState({ isOpen: true, modalMsg: "Invalid domain!" });
      this.domainContext.classList.add('is-invalid');
      return;
    }

    this.domainContext.classList.remove('is-invalid');

    const defaultLabel = this.searchTrg.innerHTML;
    this.searchTrg.innerHTML = `<div class="spinner-border" role="status"></div>`;

    this.domainContext.readOnly = this.searchTrg.disabled = true;

    this.setState({ isOpen: true, modalMsg: "Researching domain...", variant: "primary" });

    fetch(`http://localhost:8080/domain-data?domain=${domain}`)
      .then(response => response.json())
      .then(data => {
        this.tableData.body.length = 0;
        this.tableData.columnNames.length = 1;
        this.setState(this.tableData);

        this.setState({ isOpen: true, modalMsg: "Research complete.", variant: "success" });

        this.searchTrg.innerHTML = defaultLabel;
        this.domainContext.readOnly = this.searchTrg.disabled = false;

        this.setState({ excuteTime: (data.executeTime / 1000).toFixed(2) });

        for (const items of data.items) {

          for (const [key, values] of Object.entries(items)) {

            if (values.length === 0) continue;

            this.tableData.columnNames.push(key.toUpperCase());

            for (const value of values) {

              this.tableData.body.push({ data: value });
            }
          }
        }

        this.userPanel.classList.replace('my-auto', 'my-5');

        if (this.tableData.body.length === 0) {

          this.setState({ hideTable: true, hideNotFound: false });
        } else {

          this.setState(this.tableData);
          this.setState({ hideTable: false, hideNotFound: true });
        }

        setTimeout(() => this.setState({ isOpen: false, modalMsg: '', variant: '' }), 1000);

        console.log(data);
      })

      .catch(

        error => {

          this.setState({ isOpen: true, modalMsg: "Something went wrong, please try later.", variant: "danger" });

          this.searchTrg.innerHTML = defaultLabel;
          this.domainContext.readOnly = this.searchTrg.disabled = false;

          console.error(error);
        }
      );
  }

  /**
   * Handle Enter key press to trigger domain search.
   * @param {KeyboardEvent} event - The keyup event.
   */
  onKeyUpValue(event) {

    if (event.keyCode === 13) this.searchDomain(this.domainContext.value.trim());
  }

  /**
   * Close the modal.
   */
  closeModal = () => this.setState({ isOpen: false, modalMsg: '' });

  /**
   * Render the HomePage component.
   * @returns {JSX.Element} JSX Element representing the HomePage component.
   */
  render() {
    return (
      <div className="container min-vh-100 d-flex flex-column">
        <div ref={userPanel => this.userPanel = userPanel}
          className="row row-cols-1 row-cols-lg-2 w-100 my-auto">
          <div className="col col-lg-12">
            <h1 className="text-center text-white mb-5">Type domain name</h1>
          </div>
          
          <div className="col col-lg-10">
            <div className="input-group input-group-lg">
              <input
                ref={(input) => { this.domainContext = input; }}
                onKeyUp={this.onKeyUpValue.bind(this)}
                onChange={this.onKeyUpValue.bind(this)}
                type="search"
                className="form-control rounded"
                placeholder="Domain" />
            </div>
          </div>

          <div className="col col-lg-2">
            <div className="d-flex">
              <button
                ref={(btn) => { this.searchTrg = btn; }}
                onClick={this.searchDomain}
                type="button" className="btn btn-primary btn-lg mx-auto">
                <i className="fa fa-search" aria-hidden="true"></i>
                <span className="px-3">Search</span>
              </button>
            </div>
          </div>

          <div className="col col-lg-12">
            <a href="/History" >
              <button className="btn btn-outline-light my-3">
                <i className="fa fa-history" aria-hidden="true"></i>&nbsp;
                History
              </button>
            </a>
          </div>
        </div>

        <div>
          <Alert hidden={this.state.hideTable} key="primary" variant="primary" className="text-center h3 my-3">
            <i className="fa fa-clock-o" aria-hidden="true"></i>&nbsp;
            Execute time: <b>{this.state.excuteTime}</b> second.
            &nbsp;<i className="fa fa-clock-o" aria-hidden="true"></i>
          </Alert>

          <Table hidden={this.state.hideTable} responsive="lg" variant="" className="border rounded shadow text-center">
            <thead>
              <tr>
                {this.tableData.columnNames.map(name => (
                  <th key={Math.random().toString(16).slice(2)}>{name}</th>
                ))}
              </tr>
            </thead>

            <tbody>
              {this.tableData.body.map((data, index) => (
                <tr key={Math.random().toString(16).slice(2)}>
                  <td>{index + 1}</td>
                  <td>{data.data}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>

        <Alert hidden={this.state.hideNotFound} key="primary" variant="primary" className="text-center h3">
          Nothing found...
        </Alert>

        <Modal
          show={this.state.isOpen}
          onHide={this.closeModal}

          size="lg"
          aria-labelledby="contained-modal-title-vcenter"
          centered>

          <Modal.Header closeButton>
            <Modal.Title></Modal.Title>
          </Modal.Header>

          <Modal.Body>
            <Alert key={this.state.variant} variant={this.state.variant} className="mb-0 fs-3 fw-bold text-center">
              {this.state.modalMsg}
            </Alert>
          </Modal.Body>

          <Modal.Footer></Modal.Footer>
        </Modal>
      </div>
    );
  }
}

export default HomePage;