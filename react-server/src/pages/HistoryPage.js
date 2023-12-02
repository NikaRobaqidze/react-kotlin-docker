import React from "react";

// Import Bootstrap components
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import Alert from 'react-bootstrap/Alert';
import Modal from 'react-bootstrap/Modal';
import Table from 'react-bootstrap/Table';

/**
 * Component representing the HistoryPage.
 */
class HistoryPage extends React.Component {

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
     * @type {{isOpen: boolean, hideTable: boolean, hideNotFound: boolean, modalMsg: string, variant: string, isNotHistoryEmpty: boolean}}
     */
    state = {
        isOpen: false,
        hideTable: false,
        hideNotFound: true,
        modalMsg: "",
        variant: "danger",
        isNotHistoryEmpty: true
    };

    /**
     * Array to store history items.
     * @type {Array}
     */
    historyItems = []

    /**
     * Lifecycle method - executed after the component is added to the DOM.
     */
    componentDidMount = async () => {

        await fetch('http://localhost:8080/history')

            .then(response => response.json())

            .then(

                result => {

                    this.setState(this.historyItems = result);
                    this.setState({ isNotHistoryEmpty: result.length !== 0 });
                }
            )

            .catch(err => console.error("Error while rendering history", err))
    }

    /**
     * Show details in a modal based on the selected history item.
     * @param details Details of the selected history item.
     */
    showDetails = details => {

        this.tableData.body.length = 0;
        this.tableData.columnNames.length = 1;
        this.setState(this.tableData);

        const data = JSON.parse(details);

        for (const items of data) {

            for (const [key, values] of Object.entries(items)) {

                if (values.length === 0) continue;

                this.tableData.columnNames.push(key.toUpperCase());

                for (const value of values) {

                    this.tableData.body.push({ data: value });
                }
            }
        }

        if (this.tableData.body.length === 0) {

            this.setState({ hideTable: true, hideNotFound: false });

        } else {

            this.setState(this.tableData);
            this.setState({ hideTable: false, hideNotFound: true });
        }

        this.setState({ isOpen: true });
    }

    /**
     * Close the modal.
     */
    closeModal = () => this.setState({ isOpen: false });

    /**
     * Render the HistoryPage component.
     * @returns {JSX.Element} JSX Element representing the HistoryPage component.
     */
    render() {
        return (
            <div className="container text-white p-4">
                <div className="d-flex justify-content-between mt-4">
                    <a href="/" className="my-auto">
                        <button className="btn btn-light">
                            <i className="fa fa-arrow-left" aria-hidden="true"></i>&nbsp;
                            Go back
                        </button>
                    </a>

                    <h1>
                        <i className="fa fa-history" aria-hidden="true"></i>&nbsp;
                        History
                        &nbsp;<i className="fa fa-history" aria-hidden="true"></i>
                    </h1>
                </div>

                <div className="row row-cols-1 row-cols-lg-5 my-5">
                    <div className="col col-lg-12">
                        <Alert hidden={this.state.isNotHistoryEmpty} key="primary" variant="primary" className="text-center h1 w-100 p-5 animate__animated animate__rubberBand">
                            <i className="fa fa-hourglass-o" aria-hidden="true"></i>&nbsp;
                            Empty...
                        </Alert>
                    </div>

                    {this.historyItems.map(item => (
                        <div key={item.id} className="col">
                            <Card className="text-center my-3">
                                <Card.Header>
                                    <i className="fa fa-search" aria-hidden="true"></i>&nbsp;
                                    Research
                                </Card.Header>

                                <Card.Body>
                                    <Card.Title className="shadow p-2 rounded"><b>{item.domain}</b></Card.Title>
                                    
                                    <Card.Body className="mb-1">
                                        <i className="fa fa-clock-o" aria-hidden="true"></i>&nbsp;
                                        Execute: <b>{(item.executeTime / 1000).toFixed(2)}</b> sec.
                                    </Card.Body>

                                    <Button
                                        onClick={() => this.showDetails(item.details)}
                                        variant="primary"
                                        className="w-100">
                                        <i className="fa fa-info-circle" aria-hidden="true"></i>&nbsp;
                                        Details
                                        &nbsp;<i className="fa fa-info-circle" aria-hidden="true"></i>
                                    </Button>
                                </Card.Body>

                                <Card.Footer className="text-muted">
                                    <i className="fa fa-calendar-check-o" aria-hidden="true"></i>&nbsp;
                                    {new Date().getDate() === new Date(item.date).getDate() ? new Date(item.date).toLocaleTimeString() : new Date(item.date).toLocaleString()}
                                </Card.Footer>
                            </Card>
                        </div>
                    ))}
                </div>

                <Modal
                    show={this.state.isOpen}
                    onHide={this.closeModal}
                    size="lg"
                    aria-labelledby="contained-modal-title-vcenter"
                    centered>

                    <Modal.Header closeButton>
                        <Modal.Title>Researching details</Modal.Title>
                    </Modal.Header>

                    <Modal.Body>
                        <div>
                            <Table hidden={this.state.hideTable} responsive="lg" variant="" className="border rounded shadow text-center m-0">
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
                    </Modal.Body>
                    
                    <Modal.Footer></Modal.Footer>
                </Modal>
            </div>
        )
    }
}
export default HistoryPage;